package com.practicum.playlistmaker.library.ui.frags

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.library.viewmodel.NewPlaylistFragmentViewModel
import com.practicum.playlistmaker.root.RootActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }
    private val viewModel by viewModel<NewPlaylistFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // проверка для режима редактирования
        val isEditing = arguments?.getBoolean(PL_EDIT, false) ?: false
        Log.d("NewPlaylistFragment", "isEditing: $isEditing")
        if (isEditing) {
            // переносим данные для редакции
            val playlistId = arguments?.getLong(PLAYLIST_ID, -1)
            val playlistName = arguments?.getString(PL_NAME, "")
            val playlistDescription = arguments?.getString(PL_DESC, "")
            val playlistCoverPath = arguments?.getString(PL_COVER_PATH, "")
            Log.d(
                "NewPlaylistFragment",
                "Editing playlist: id=$playlistId, name=$playlistName, description=$playlistDescription"
            )

            viewModel.loadPlaylistForEditing(
                playlistId,
                playlistName ?: "",
                playlistDescription ?: "",
                playlistCoverPath
            )
            // обновляем UI в режиме редактирования
            binding.toolbarNewPL.title = getString(R.string.edit_playlist)
            binding.newPlaylistButton.text = getString(R.string.save)
            // подгружаем картинку
            if (!playlistCoverPath.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(playlistCoverPath)
                    .transform(CenterCrop())
                    .placeholder(R.drawable.ic_placeholder_312_x_312)
                    .into(binding.imageNewPL)
            }
        } else {
            // иначе создаём новый плейлист
            binding.toolbarNewPL.title = getString(R.string.new_playlist)
            binding.newPlaylistButton.text = getString(R.string.create)
        }



        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            // обновили ui в зависимости от состояния
            if (binding.inputNameNewPL.text.toString() != state.namePL) {
                binding.inputNameNewPL.setText(state.namePL)
            }
            if (binding.inputDescriptionNewPL.text.toString() != state.descriptionPL) {
                binding.inputDescriptionNewPL.setText(state.descriptionPL)
            }
            // меняем состояние кнопки "Создать" или "Сохранить"
            val color = if (state.isSaveButtinEnabled) {
                ContextCompat.getColor(requireContext(), R.color.blue)
            } else {
                ContextCompat.getColor(requireContext(), R.color.gray)
            }
            binding.newPlaylistButton.backgroundTintList = ColorStateList.valueOf(color)
            binding.newPlaylistButton.isEnabled = state.isSaveButtinEnabled
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
                if (imageUri != null) {
                    Glide.with(requireContext())
                        .load(imageUri)
                        .transform(CenterCrop())
                        .into(binding.imageNewPL)
                    saveImageToPrivateStorage(imageUri)
                }
            }

        binding.imageNewPL.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.newPlaylistButton.setOnClickListener {
            viewModel.savePlaylist()
            var playlistName = binding.inputNameNewPL.text.toString()
            if (viewModel.isEditingMode()) {
                setSnackbar(getString(R.string.playlist_updated_success, playlistName))
            } else {
                setSnackbar(getString(R.string.playlist_created_success, playlistName))
            }
            toBack()
        }

        binding.inputNameNewPL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                viewModel.editNamePL(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputDescriptionNewPL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                viewModel.editDescriptionPL(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        requireActivity().onBackPressedDispatcher.addCallback(  // привязка к жизненному циклу фрагмента, предотвращение утечек памяти, гарантия не активности
            viewLifecycleOwner,                                 // колбека, когда нет фрагмента
            object :
                OnBackPressedCallback(true) {              // получение состояние лайвдаты единожды, т.е. считывается раз, когда колбек зареган
                override fun handleOnBackPressed() {            // колбек не реагирует на изменения лайвдаты, поэтому менее реактивный (прошлая версия была противоложна по действию)
                    val state = viewModel.stateLiveData.value
                    if (state != null && (state.namePL.isNotEmpty() || state.descriptionPL.isNotEmpty() || state.coverPathPL.isNotEmpty())) {
                        showDialog()
                    } else {
                        toBack()
                    }
                }
            })

        binding.toolbarNewPL.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun saveImageToPrivateStorage(imageUri: Uri) { // надо будет вынести во вьюМодел
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val inputStream: InputStream? =
                    requireContext().contentResolver.openInputStream(imageUri) // получили поток из URI картинки
                val directory = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "playlistmaker"
                ) // создаём директорию, где будет находиться картинка
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val fileName = "${System.currentTimeMillis()}"
                val file = File(directory, fileName) // доступ к файлу
                val outputStream: OutputStream =
                    FileOutputStream(file) // копируем данные из входящего потока в File
                val buffer = ByteArray(4 * 1024) // буффер для записи, 4 кб
                var bytesRead: Int // храним байты из входящего потока
                while (inputStream?.read(buffer) // читаем инфу из входящего потока, проверяем на null
                        .let { bytesRead = it ?: -1; it != -1 }
                ) { // если не null, то bytesRead = it, иначе вернёт -1, и сразу исключаем этот вариант для завершения цикла
                    outputStream.write(
                        buffer,
                        0,
                        bytesRead
                    ) // буффер / индекс начала записи / кол-во байтов для записи из буффера
                }
                outputStream.flush()
                outputStream.close()
                inputStream?.close()
                val savedUri = Uri.fromFile(file)
                withContext(Dispatchers.Main) { viewModel.editCoverPathPL(savedUri.toString()) }
            }
        }
    }

    private fun toBack() {
        if (activity is RootActivity) {
            findNavController().navigateUp()
        } else {
            val container =
                requireActivity().findViewById<FragmentContainerView>(R.id.fragmentContainerView)
            if (container.isVisible == true) {
                fragmentManager?.popBackStack()
            }
            container.isVisible = false
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.finish_creating_playlist))
            .setMessage(getString(R.string.all_unsaves_data_will_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.nope)) { dialog, which ->
                findNavController().popBackStack()
            }
            .show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("RestrictedApi")
    private fun setSnackbar(additionalMessage: String) {

        val inflater = LayoutInflater.from(view?.context)
        val snackbarLayout: View = inflater.inflate(R.layout.snackbar, null)
        val snackbarText: TextView = snackbarLayout.findViewById(R.id.snackbar_text)
        snackbarText.text = additionalMessage

        val snackbar =
            view?.let {
                Snackbar.make(
                    it,
                    "",
                    Snackbar.LENGTH_SHORT
                )
            } // специально остаётся пустая строка

        val snackbarView = snackbar?.view as Snackbar.SnackbarLayout
        snackbarView.setPadding(0, 0, 0, 0) // Удаляем стандартные отступы
        ViewCompat.setBackgroundTintList(
            snackbarView,
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.day_dark_and_night_white
                )
            )
        )
        snackbarView.addView(snackbarLayout, 0)
        snackbar.show()
    }

    companion object {
        private const val PLAYLIST_ID = "playlist_id"
        private const val PL_NAME = "playlist_name"
        private const val PL_DESC = "playlist_description"
        private const val PL_COVER_PATH = "playlist_cover_path"
        private const val PL_EDIT = "is_editing"
    }
}