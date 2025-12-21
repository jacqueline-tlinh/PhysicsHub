package com.physicshub.backend.controller

import com.physicshub.backend.model.TranslationResponse
import com.physicshub.backend.model.TranslationStrings
import com.physicshub.backend.model.UpdateTranslationRequest
import com.physicshub.backend.service.TranslationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"])
class TranslationController(
    private val translationService: TranslationService
) {

    @GetMapping("/translations")
    fun getAllTranslations(): ResponseEntity<TranslationResponse> {
        return ResponseEntity.ok(translationService.getAllTranslations())
    }

    @GetMapping("/translations/{language}")
    fun getTranslationsByLanguage(@PathVariable language: String): ResponseEntity<TranslationStrings> {
        val translations = translationService.getTranslationsByLanguage(language)
        return if (translations != null) {
            ResponseEntity.ok(translations)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/translations")
    fun updateAllTranslations(@RequestBody translations: TranslationResponse): ResponseEntity<TranslationResponse> {
        val updated = translationService.updateAllTranslations(translations)
        return ResponseEntity.ok(updated)
    }

    @PutMapping("/translations/{language}")
    fun updateLanguageTranslations(
        @PathVariable language: String,
        @RequestBody strings: TranslationStrings
    ): ResponseEntity<Map<String, Any>> {
        val success = translationService.updateLanguageTranslations(language, strings)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true, "message" to "Translations updated"))
        } else {
            ResponseEntity.badRequest().body(mapOf("success" to false, "message" to "Invalid language"))
        }
    }

    @PatchMapping("/translations")
    fun updateSingleTranslation(@RequestBody request: UpdateTranslationRequest): ResponseEntity<Map<String, Any>> {
        val success = translationService.updateTranslation(request.language, request.key, request.value)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true, "message" to "Translation updated"))
        } else {
            ResponseEntity.badRequest().body(mapOf("success" to false, "message" to "Invalid language or key"))
        }
    }

    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "OK", "service" to "PhysicsHub Translation API"))
    }
}
