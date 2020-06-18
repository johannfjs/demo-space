package com.example.demo

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*


@RestController
@RequestMapping("/demo")
class DemoController {
    @RequestMapping(method = [RequestMethod.POST])
    fun save(@RequestParam("name") name: String): String {
        return uploadBase64ToStorage(1, "/Users/johann/Desktop/demo.png", name)
    }

    fun uploadBase64ToStorage(userid: Int, location: String, strimage: String): String {
        val file = File(location)
        if (file.exists()) {
            val bytesArray = ByteArray(file.length().toInt())
            val fis = FileInputStream(file)
            fis.read(bytesArray)
            fis.close()

            val awscp = AWSStaticCredentialsProvider(
                    BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY")
            )
            val space = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(awscp)
                    .withEndpointConfiguration(
                            AwsClientBuilder.EndpointConfiguration("ENDPOINT", "REGION")
                    )
                    .build()
            //val files = space.listObjects("test-dev")
            val name = UUID.randomUUID().toString()
            space.putObject(PutObjectRequest("BUCKET_NAME", name, file).withCannedAcl(CannedAccessControlList.PublicRead))
            return space.getUrl("BUCKET_NAME", name).toString()
        }
        return ""
    }
}