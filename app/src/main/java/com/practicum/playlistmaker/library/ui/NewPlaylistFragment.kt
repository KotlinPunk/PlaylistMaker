package com.practicum.playlistmaker.library.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.library.viewmodel.NewPlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import com.practicum.playlistmaker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            // обновили ui в зависимости от состояния
            if (binding.inputNameNewPL.text.toString() != state.namePL) {
                binding.inputNameNewPL.setText(state.namePL)
            }
            if (binding.inputDescriptionNewPL.text.toString() != state.descriptionPL) {
                binding.inputDescriptionNewPL.setText(state.descriptionPL)
            }
            // меняем состояние кнопки "Создать"
            val color = if (state.isSaveButtinEnabled) {
                ContextCompat.getColor(requireContext(), R.color.blue)
            } else {
                ContextCompat.getColor(requireContext(), R.color.gray)
            }
            binding.newPlaylistButton.backgroundTintList = ColorStateList.valueOf(color)
            binding.newPlaylistButton.isEnabled = state.isSaveButtinEnabled

            if (state.saveResult != null) {
                Toast.makeText(
                    requireContext(),
                    "Плейлист ${state.namePL} создан",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }
            if (state.saveError != null) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка при сохранении",
                    Toast.LENGTH_LONG
                ).show()
            }
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
        }

        binding.inputNameNewPL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.clearNameNewPL.visibility = clearIconVisibility(s)
                viewModel.editNamePL(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputDescriptionNewPL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.clearDescriptionNewPL.visibility = clearIconVisibility(s)
                viewModel.editDescriptionPL(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) { // true означает, что callback активен по умолчанию
            override fun handleOnBackPressed() {
                backToFragment()
                isEnabled = false // Отключить callback после обработки
            }
        })

        binding.clearNameNewPL.setOnClickListener {
            binding.inputNameNewPL.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.inputNameNewPL.windowToken, 0)
        }

        binding.clearDescriptionNewPL.setOnClickListener {
            binding.inputNameNewPL.setText("")
            inputMethodManager?.hideSoftInputFromWindow(
                binding.clearDescriptionNewPL.windowToken,
                0
            )
        }

        binding.toolbarNewPL.setNavigationOnClickListener {
            backToFragment()
        }

    }

    private fun saveImageToPrivateStorage(imageUri: Uri) {
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

    private fun clearIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun backToFragment() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            if (state.namePL.isNotEmpty() || state.descriptionPL.isNotEmpty() || state.coverPathPL.isNotEmpty()) {
                showDialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton("Нет") { dialog, which ->
                findNavController().popBackStack()
            }
            .show()
    }
}


