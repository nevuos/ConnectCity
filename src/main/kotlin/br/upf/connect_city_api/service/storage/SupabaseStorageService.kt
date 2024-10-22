package br.upf.connect_city_api.service.storage

import br.upf.connect_city_api.util.constants.storage.StorageConstants
import br.upf.connect_city_api.util.constants.storage.StorageMessageConstants
import br.upf.connect_city_api.util.exception.StorageException
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class SupabaseStorageService(
    private val s3Client: S3Client
) : StorageService {

    @Value("\${supabase.storage.bucket.name}")
    private lateinit var bucketName: String

    @Value("\${supabase.storage.public.url}")
    private lateinit var supabasePublicUrl: String

    @Async
    override fun uploadFileAsync(file: MultipartFile): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            try {
                // Gerando a chave do arquivo (file key)
                val key = "${StorageConstants.UPLOADS_DIRECTORY}${StorageConstants.KEY_SEPARATOR}${UUID.randomUUID()}_${file.originalFilename}"
                val tempFile = Files.createTempFile(StorageConstants.TEMP_FILE_PREFIX, file.originalFilename).toFile()
                file.transferTo(tempFile)

                // Criando a requisição para subir o arquivo
                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile))

                // Gerando a URL correta para o arquivo público no Supabase (corrigindo a barra extra)
                val fileUrl = "${supabasePublicUrl.trimEnd('/')}/$bucketName/$key"

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
                val urlPrefix = "${supabasePublicUrl.trimEnd('/')}/$bucketName${StorageConstants.KEY_SEPARATOR}"
                val key = url.removePrefix(urlPrefix)
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

    @Async
    override fun deleteFile(fileUrl: String): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            try {
                val urlPrefix = "${supabasePublicUrl}$bucketName${StorageConstants.KEY_SEPARATOR}"
                val key = fileUrl.removePrefix(urlPrefix)
                val deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                s3Client.deleteObject(deleteObjectRequest)
            } catch (e: Exception) {
                throw StorageException("${StorageMessageConstants.DELETE_ERROR_MESSAGE}: ${e.message}")
            }
        }
    }
}