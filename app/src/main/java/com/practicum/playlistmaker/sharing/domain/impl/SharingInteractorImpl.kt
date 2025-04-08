package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.intr.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.repo.SharingRepository

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareAppIntr() {
        sharingRepository.shareAppRepo()
    }

    override fun openTermsIntr() {
        sharingRepository.openTermsRepo()
    }

    override fun openSupportIntr() {
        sharingRepository.openSupportRepo()
    }
}