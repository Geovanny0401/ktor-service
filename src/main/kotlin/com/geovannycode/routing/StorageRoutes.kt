package com.geovannycode.routing

import com.geovannycode.services.StorageService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

private const val END_POINT = "api/storage"
fun Application.storageRoutes() {
    val storageService: StorageService by inject()
    routing {
        route("/$END_POINT") {
            get("{name}", {
                description = "Get by name file"
                request {
                    pathParameter<String>("name") {
                        description = "Name file find"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Success"
                        body<File> { description = "url file request" }
                    }
                    HttpStatusCode.NotFound to {
                        description = "File not found"
                        body<String> { description = "exception" }
                    }
                }
            }) {
                try {
                    val name = call.parameters["name"].toString()
                    val file = storageService.getFile(name)

                    call.respond(HttpStatusCode.OK, file.toString())
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post({
                description = "Post file"
                response {
                    HttpStatusCode.OK to {
                        description = "File save success"
                        body<File> { description = "file success" }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Error upload file"
                        body<String> { description = "Exception" }
                    }
                }
            }) {
                try {
                    val readChannel = call.receiveChannel()
                    val fileName = UUID.randomUUID().toString() + ".png"
                    val res = storageService.saveFile(fileName, readChannel)
                    call.respond(HttpStatusCode.OK, res)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }
        }
    }
}
