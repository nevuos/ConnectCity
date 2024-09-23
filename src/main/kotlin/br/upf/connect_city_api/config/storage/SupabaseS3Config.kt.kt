package br.upf.connect_city_api.config.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class SupabaseS3Config {

    @Value("\${supabase.access_key}")
    private lateinit var accessKey: String

    @Value("\${supabase.secret_key}")
    private lateinit var secretKey: String

    @Value("\${supabase.storage.endpoint}")
    private lateinit var endpoint: String

    @Value("\${supabase.region}")
    private lateinit var region: String

    @Bean
    fun createS3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(buildAwsCredentials()))
            .endpointOverride(URI(endpoint))
            .build()
    }

    private fun buildAwsCredentials(): AwsBasicCredentials {
        return AwsBasicCredentials.create(accessKey, secretKey)
    }
}