package com.machinedashboard

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element

/** Regression checks for the foundation's permission and local-data boundary. */
class ManifestSecurityTest {
    private fun manifest(): Element {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }
        return factory.newDocumentBuilder()
            .parse(File(requireNotNull(System.getProperty("manifestPath"))))
            .documentElement
    }

    @Test
    fun foundationRequestsNoDeviceOrNetworkPermissions() {
        val manifest = manifest()
        assertEquals(0, manifest.getElementsByTagName("uses-permission").length)
        assertEquals(0, manifest.getElementsByTagName("uses-permission-sdk-23").length)
    }

    @Test
    fun privateApplicationDataIsExcludedFromAndroidBackup() {
        val application = manifest().getElementsByTagName("application").item(0) as Element
        assertEquals(
            "false",
            application.getAttributeNS("http://schemas.android.com/apk/res/android", "allowBackup")
        )
        assertEquals(
            "false",
            application.getAttributeNS(
                "http://schemas.android.com/apk/res/android",
                "fullBackupContent"
            )
        )
        assertEquals(
            "@xml/data_extraction_rules",
            application.getAttributeNS(
                "http://schemas.android.com/apk/res/android",
                "dataExtractionRules"
            )
        )
    }

    @Test
    fun cloudBackupAndDeviceTransferExcludeAllPersistentStorage() {
        val rules = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File(requireNotNull(System.getProperty("backupRulesPath"))))
        val expectedDomains = setOf(
            "root", "file", "database", "sharedpref", "external",
            "device_root", "device_file", "device_database", "device_sharedpref"
        )
        for (sectionName in listOf("cloud-backup", "device-transfer")) {
            val sections = rules.getElementsByTagName(sectionName)
            assertEquals(1, sections.length)
            val section = sections.item(0) as Element
            assertEquals(0, section.getElementsByTagName("include").length)
            val exclusions = section.getElementsByTagName("exclude")
            val actualDomains = (0 until exclusions.length).map { index ->
                val exclusion = exclusions.item(index) as Element
                assertEquals(".", exclusion.getAttribute("path"))
                exclusion.getAttribute("domain")
            }.toSet()
            assertEquals(expectedDomains, actualDomains)
        }
    }
}
