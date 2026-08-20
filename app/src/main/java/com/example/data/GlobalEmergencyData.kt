package com.example.data

data class EmergencyNumber(
    val id: String,
    val title: String,
    val number: String,
    val category: String, // Police, Medical, Fire, Crisis, Disaster
    val countryOrRegion: String,
    val regionCode: String, // US, EU, UK, ASIA, LATAM, GLOBAL
    val description: String,
    val iconType: String // POLICE, MEDICAL, FIRE, CRISIS, GENERAL
)

object GlobalEmergencyData {
    val regions = listOf(
        "ALL" to "All Regions",
        "US" to "United States & Canada",
        "EU" to "European Union",
        "UK" to "United Kingdom",
        "ASIA" to "Asia & Oceania",
        "LATAM" to "Latin America"
    )

    val numbers = listOf(
        // US & Canada
        EmergencyNumber(
            id = "us_911",
            title = "All Emergency (911)",
            number = "911",
            category = "Universal Emergency",
            countryOrRegion = "United States & Canada",
            regionCode = "US",
            description = "Immediate dispatch for Police, Fire, and Paramedics across USA & Canada.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "us_988",
            title = "Suicide & Crisis Lifeline",
            number = "988",
            category = "Mental Health Crisis",
            countryOrRegion = "United States & Canada",
            regionCode = "US",
            description = "Free, confidential 24/7 support for people in distress, emotional support, and prevention resources.",
            iconType = "CRISIS"
        ),
        EmergencyNumber(
            id = "us_poison",
            title = "Poison Control Center",
            number = "18002221222",
            category = "Toxicology / Poison",
            countryOrRegion = "United States",
            regionCode = "US",
            description = "Fast, expert advice for accidental poison ingestion, chemical contact, or bites.",
            iconType = "MEDICAL"
        ),
        EmergencyNumber(
            id = "us_311",
            title = "Non-Emergency Municipal",
            number = "311",
            category = "City Services",
            countryOrRegion = "United States & Canada",
            regionCode = "US",
            description = "Local government inquiries, road blockages, non-life-threatening community issues.",
            iconType = "GENERAL"
        ),

        // European Union
        EmergencyNumber(
            id = "eu_112",
            title = "European Universal Emergency",
            number = "112",
            category = "Universal Emergency",
            countryOrRegion = "European Union (All Member States)",
            regionCode = "EU",
            description = "Standard single emergency number valid anywhere in the European Union.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "eu_missing_children",
            title = "Missing Children Hotline",
            number = "116000",
            category = "Child Protection",
            countryOrRegion = "European Union",
            regionCode = "EU",
            description = "Pan-European hotline to report missing or abducted children.",
            iconType = "CRISIS"
        ),
        EmergencyNumber(
            id = "fr_samu",
            title = "SAMU Medical Emergency (France)",
            number = "15",
            category = "Ambulance / Medical",
            countryOrRegion = "France",
            regionCode = "EU",
            description = "Urgent medical assistance and paramedic dispatch in France.",
            iconType = "MEDICAL"
        ),
        EmergencyNumber(
            id = "de_police",
            title = "Polizei (Germany)",
            number = "110",
            category = "Police",
            countryOrRegion = "Germany",
            regionCode = "EU",
            description = "Direct Federal Police emergency dispatch line in Germany.",
            iconType = "POLICE"
        ),

        // United Kingdom
        EmergencyNumber(
            id = "uk_999",
            title = "UK Emergency Services (999)",
            number = "999",
            category = "Universal Emergency",
            countryOrRegion = "United Kingdom",
            regionCode = "UK",
            description = "Primary line for Police, Ambulance, Fire Brigade, and Coastguard.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "uk_111",
            title = "NHS Urgent Medical Advice",
            number = "111",
            category = "Urgent Medical",
            countryOrRegion = "United Kingdom",
            regionCode = "UK",
            description = "Free National Health Service advice when you need urgent medical help but it's not life-threatening.",
            iconType = "MEDICAL"
        ),
        EmergencyNumber(
            id = "uk_101",
            title = "Police Non-Emergency",
            number = "101",
            category = "Police Non-Urgent",
            countryOrRegion = "United Kingdom",
            regionCode = "UK",
            description = "Report minor crime or contact local police when an immediate response is not required.",
            iconType = "POLICE"
        ),

        // Asia & Oceania
        EmergencyNumber(
            id = "au_000",
            title = "Triple Zero (000)",
            number = "000",
            category = "Universal Emergency",
            countryOrRegion = "Australia",
            regionCode = "ASIA",
            description = "Main national emergency service number in Australia for Police, Fire, and Ambulance.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "jp_110",
            title = "Police (Japan)",
            number = "110",
            category = "Police",
            countryOrRegion = "Japan",
            regionCode = "ASIA",
            description = "Emergency police dispatch line throughout Japan.",
            iconType = "POLICE"
        ),
        EmergencyNumber(
            id = "jp_119",
            title = "Fire & Ambulance (Japan)",
            number = "119",
            category = "Fire & Medical",
            countryOrRegion = "Japan",
            regionCode = "ASIA",
            description = "Emergency medical rescue and fire fighting service line throughout Japan.",
            iconType = "FIRE"
        ),
        EmergencyNumber(
            id = "in_112",
            title = "National Emergency Response (India)",
            number = "112",
            category = "Universal Emergency",
            countryOrRegion = "India",
            regionCode = "ASIA",
            description = "All-in-one Emergency Response Support System (ERSS) across India.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "sg_995",
            title = "SCDF Ambulance & Fire (Singapore)",
            number = "995",
            category = "Fire & Medical",
            countryOrRegion = "Singapore",
            regionCode = "ASIA",
            description = "Singapore Civil Defence Force emergency ambulance and fire rescue.",
            iconType = "MEDICAL"
        ),
        EmergencyNumber(
            id = "ph_911",
            title = "Emergency 911 (Philippines)",
            number = "911",
            category = "Universal Emergency",
            countryOrRegion = "Philippines",
            regionCode = "ASIA",
            description = "National Emergency 911 Executive hotline for nationwide assistance.",
            iconType = "GENERAL"
        ),

        // Latin America
        EmergencyNumber(
            id = "mx_911",
            title = "Emergencias 911 (Mexico)",
            number = "911",
            category = "Universal Emergency",
            countryOrRegion = "Mexico",
            regionCode = "LATAM",
            description = "Unified national emergency number in Mexico for Police, Red Cross, and Fire.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "br_190",
            title = "Polícia Militar (Brazil)",
            number = "190",
            category = "Police",
            countryOrRegion = "Brazil",
            regionCode = "LATAM",
            description = "Military Police emergency response throughout Brazil.",
            iconType = "POLICE"
        ),
        EmergencyNumber(
            id = "br_192",
            title = "SAMU Ambulância (Brazil)",
            number = "192",
            category = "Ambulance / Medical",
            countryOrRegion = "Brazil",
            regionCode = "LATAM",
            description = "Mobile emergency medical care service (SAMU) in Brazil.",
            iconType = "MEDICAL"
        ),
        EmergencyNumber(
            id = "ar_911",
            title = "Emergencias 911 (Argentina)",
            number = "911",
            category = "Universal Emergency",
            countryOrRegion = "Argentina",
            regionCode = "LATAM",
            description = "National unified emergency response line in Argentina.",
            iconType = "GENERAL"
        ),
        EmergencyNumber(
            id = "cl_133",
            title = "Carabineros de Chile",
            number = "133",
            category = "Police",
            countryOrRegion = "Chile",
            regionCode = "LATAM",
            description = "Emergency police assistance line in Chile.",
            iconType = "POLICE"
        )
    )
}
