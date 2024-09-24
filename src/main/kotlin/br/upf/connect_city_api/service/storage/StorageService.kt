package br.upf.connect_city_api.service.storage

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.concurrent.CompletableFuture

interface StorageService {
    fun uploadFileAsync(file: MultipartFile): CompletableFuture<String>
    fun downloadFileAsync(url: String): CompletableFuture<File>
    fun deleteFile(fileUrl: String): CompletableFuture<Void>
}