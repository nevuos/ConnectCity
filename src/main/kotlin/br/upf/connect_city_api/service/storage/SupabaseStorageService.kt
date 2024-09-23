package br.upf.connect_city_api.service.storage

import br.upf.connect_city_api.util.constants.storage.StorageMessageConstants
import br.upf.connect_city_api.util.constants.storage.StorageConstants
import br.upf.connect_city_api.util.exception.StorageException
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Service
class SupabaseStorageService(
    private val s3Client: S3Client
) : StorageService {

    @Value("\${supabase.storage.bucket.name}")
    private lateinit var bucketName: String

    @Async
    override fun uploadFileAsync(file: MultipartFile): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            try {
                val key = "${StorageConstants.UPLOADS_DIRECTORY}${StorageConstants.KEY_SEPARATOR}${UUID.randomUUID()}_${file.originalFilename}"
                val tempFile = Files.createTempFile(StorageConstants.TEMP_FILE_PREFIX, file.originalFilename).toFile()
                file.transferTo(tempFile)

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile))

                val fileUrl = "${s3Client.utilities().getUrl { it.bucket(bucketName).key(key) }}"

                tempFile.delete()

                fileUrl
            } catch (e: Exception) {
                throw StorageException("${StorageMessageConstants.UPLOAD_ERROR_MESSAGE}: ${e.message}")
            }
        }
    }

    @Async
    override fun downloadFileAsync(url: String): CompletableFuture<File> {
        return CompletableFuture.supplyAsync {
            try {
                val key = url.removePrefix("${s3Client.utilities().getUrl { it.bucket(bucketName) }}${StorageConstants.KEY_SEPARATOR}")
                val getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                val response: ResponseInputStream<GetObjectResponse> = s3Client.getObject(getObjectRequest)
                val tempFile = Files.createTempFile(StorageConstants.TEMP_FILE_PREFIX, key.replace(StorageConstants.KEY_SEPARATOR, StorageConstants.TEMP_FILE_SUFFIX)).toFile()

                FileOutputStream(tempFile).use { outputStream ->
                    response.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                tempFile
            } catch (e: Exception) {
                throw StorageException("${StorageMessageConstants.DOWNLOAD_ERROR_MESSAGE}: ${e.message}")
            }
        }
    }
}