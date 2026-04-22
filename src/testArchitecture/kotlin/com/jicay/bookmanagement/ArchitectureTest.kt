package com.jicay.bookmanagement

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec


class ArchitectureTest : FunSpec({

    val basePackage = "com.jicay.bookmanagement"

    test("it should respect the clean architecture concept") {
        val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(basePackage)

        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("Infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Driving").definedBy("$basePackage.infrastructure.driving..")
            .layer("Driven").definedBy("$basePackage.infrastructure.driven..")
            .layer("Application").definedBy("$basePackage.infrastructure.application..")
            .layer("Domain").definedBy("$basePackage.domain..")
            .layer("Standard API")
            .definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..")
            .withOptionalLayers(true)
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Driving").mayOnlyBeAccessedByLayers("Application")
            .whereLayer("Driven").mayOnlyBeAccessedByLayers("Application")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Driving", "Driven")
            .whereLayer("Domain").mayOnlyAccessLayers("Standard API")

        rule.check(importedClasses)
    }
})
