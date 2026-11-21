
package com.example.safisha.domain.model

object IssueCategory {

    const val ROAD_DAMAGE = "ROAD_DAMAGE"
    const val STREET_LIGHTING = "STREET_LIGHTING"
    const val TRAFFIC_AND_PARKING = "TRAFFIC_AND_PARKING"
    const val WASTE = "WASTE"
    const val DRAINAGE = "DRAINAGE"
    const val SEWAGE = "SEWAGE"
    const val WATER_SUPPLY = "WATER_SUPPLY"
    const val PUBLIC_FACILITY = "PUBLIC_FACILITY"
    const val ILLEGAL_CONSTRUCTION = "ILLEGAL_CONSTRUCTION"
    const val NOISE_AND_NUISANCE = "NOISE_AND_NUISANCE"

    data class CategorySpec(
        val id: String,
        val displayTitle: String,
        val description: String,
        val includes: String,
        val excludes: String,
        val suggestedDepartment: String
    )

    val specs = listOf(

        CategorySpec(
            id = ROAD_DAMAGE,
            displayTitle = "Road Damage & Potholes",
            description = "Damage affecting roads and their physical condition.",
            includes = "Potholes, eroded tarmac, washed-out murram roads, damaged speed bumps and broken guardrails.",
            excludes = "Flooding caused by blocked drains (Drainage); faulty traffic signals (Traffic & Parking).",
            suggestedDepartment = "Mobility & Works"
        ),

        CategorySpec(
            id = STREET_LIGHTING,
            displayTitle = "Street & Public Lighting",
            description = "Faults affecting public lighting infrastructure.",
            includes = "Broken, flickering or knocked-down streetlamps and high-mast floodlights.",
            excludes = "Traffic signals and intersection lights (Traffic & Parking).",
            suggestedDepartment = "Mobility & Works – Electrical"
        ),

        CategorySpec(
            id = TRAFFIC_AND_PARKING,
            displayTitle = "Traffic Signals & Parking",
            description = "Problems affecting traffic control, road signage and public parking.",
            includes = "Malfunctioning traffic lights, faded zebra crossings, missing road signs, illegal parking and matatu stage obstruction.",
            excludes = "Physical road surface damage (Road Damage).",
            suggestedDepartment = "Mobility & Traffic Enforcement"
        ),

        CategorySpec(
            id = WASTE,
            displayTitle = "Garbage & Solid Waste",
            description = "Problems involving the collection and disposal of solid waste.",
            includes = "Uncollected refuse, overflowing skips and illegal roadside dumping.",
            excludes = "Waste blocking drainage infrastructure where drainage failure is the primary problem (Drainage); human waste and effluent (Sewage).",
            suggestedDepartment = "Environment / Solid Waste Management"
        ),

        CategorySpec(
            id = DRAINAGE,
            displayTitle = "Stormwater & Flooded Drains",
            description = "Problems affecting stormwater drainage and rainwater movement.",
            includes = "Clogged roadside trenches, blocked culverts, missing drain covers and stagnant rainwater flooding.",
            excludes = "Foul-smelling wastewater and human waste (Sewage); clean piped water leaks (Water Supply).",
            suggestedDepartment = "Mobility & Works / Environment"
        ),

        CategorySpec(
            id = SEWAGE,
            displayTitle = "Sewerage & Effluent",
            description = "Problems involving wastewater and sewage infrastructure.",
            includes = "Burst sewer pipes, overflowing sewer manholes and raw sewage discharge.",
            excludes = "Rainwater flooding (Drainage); clean water pipe bursts (Water Supply).",
            suggestedDepartment = "Nairobi Water & Sewerage"
        ),

        CategorySpec(
            id = WATER_SUPPLY,
            displayTitle = "Clean Water Supply",
            description = "Problems involving the availability and distribution of clean water.",
            includes = "Burst clean-water mains, dry taps, unscheduled rationing, leaking water meters and illegal water connections.",
            excludes = "Wastewater and sewer overflows (Sewage); rainwater drainage (Drainage).",
            suggestedDepartment = "Nairobi Water & Sewerage"
        ),

        CategorySpec(
            id = PUBLIC_FACILITY,
            displayTitle = "Public Facilities & Amenities",
            description = "Maintenance problems affecting public buildings and community amenities.",
            includes = "Damaged public toilets, deteriorating public buildings, damaged park equipment and maintenance problems at public facilities.",
            excludes = "Problems that belong to a more specific category, such as broken lighting (Street Lighting), sewage (Sewage) or water supply (Water Supply).",
            suggestedDepartment = "Relevant Public Facilities Department"
        ),

        CategorySpec(
            id = ILLEGAL_CONSTRUCTION,
            displayTitle = "Illegal Construction & Encroachment",
            description = "Suspected unauthorised construction and encroachment on protected public spaces.",
            includes = "Suspected unapproved buildings, riparian reserve encroachment and construction on road reserves or wayleaves.",
            excludes = "Noise complaints (Noise & Nuisance).",
            suggestedDepartment = "Built Environment & Urban Planning"
        ),

        CategorySpec(
            id = NOISE_AND_NUISANCE,
            displayTitle = "Noise & Public Nuisance",
            description = "Activities causing excessive noise or disruption in public spaces.",
            includes = "Excessive noise from clubs, events and worship venues, sidewalk obstruction by hawkers and other public nuisances.",
            excludes = "Permanent illegal structures (Illegal Construction); vehicles obstructing roads (Traffic & Parking).",
            suggestedDepartment = "Environment / County Inspectorate"
        )
    )

    fun getById(id: String): CategorySpec? {
        return specs.firstOrNull { it.id == id }
    }
}
