package com.biologger.data
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.os.Bundle

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- MODULE 1: FOREST CAPTURE ---

@Entity(tableName = "tree_measurements")
data class TreeMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val treeId: String,
    val speciesName: String,
    val dbh: Double,
    val height: Double,
    val crownNS: Double,
    val crownEW: Double,
    val date: String,
    val observer: String,
    val notes: String?,
    val basalArea: Double,
    val crossSectionalArea: Double,
    val meanCrownDiameter: Double,
    val crownArea: Double,
    val crownRatio: Double,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "basal_area_plots")
data class BasalAreaPlot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val plotShape: String,
    val plotSize: Double,
    val plotSizeUnit: String,
    val treeDbhListJson: String, // JSON array of DBH values
    val totalBasalArea: Double,
    val basalAreaPerHa: Double,
    val treesPerHa: Double,
    val meanDbh: Double,
    val qmd: Double,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stand_density")
data class StandDensityResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val treesPerHa: Double,
    val qmd: Double,
    val speciesGroup: String,
    val maxSdi: Double,
    val sdi: Double,
    val relativeDensity: Double,
    val competitionZone: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "timber_volume")
data class TimberVolume(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val logId: String,
    val species: String,
    val length: Double,
    val dBase: Double,
    val dMid: Double,
    val dTop: Double,
    val barkThickness: Double,
    val volumeHuber: Double,
    val volumeSmalian: Double,
    val volumeNewton: Double,
    val formFactor: Double,
    val formulaUsed: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "biomass_results")
data class BiomassResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val treeId: String,
    val species: String,
    val dbh: Double,
    val height: Double,
    val woodDensity: Double,
    val equationName: String,
    val carbonFraction: Double,
    val co2Factor: Double,
    val agbKg: Double,
    val agbMg: Double,
    val carbonStockKg: Double,
    val co2EqKg: Double,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tree_health")
data class TreeHealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val treeId: String,
    val species: String,
    val plotId: String,
    val date: String,
    val status: String,
    val damageTypes: String, // Comma-separated
    val severity: Int,
    val dieback: Double,
    val symptoms: String, // Comma-separated
    val photoPath: String?,
    val healthScore: Double,
    val healthCategory: String,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "crown_cover")
data class CrownCoverRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val method: String,
    val totalPoints: Int?,
    val hitPoints: Int?,
    val densiometerReadings: String?, // JSON array
    val visualCover: Double?,
    val layer: String?,
    val coverPercent: Double,
    val opennessPercent: Double,
    val lightCategory: String,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "litterfall")
data class LitterfallRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val trapId: String,
    val trapArea: Double,
    val date: String,
    val intervalDays: Int,
    val fractionMassesJson: String, // JSON map {fraction: mass}
    val moistureCorrection: Double,
    val totalDryMass: Double,
    val rate: Double,
    val annualFluxG: Double,
    val annualFluxMg: Double,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quadrat_studies")
data class QuadratStudy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val quadratId: String,
    val size: Double,
    val layer: String,
    val speciesDataJson: String, // JSON array of species objects
    val richness: Int,
    val shannonH: Double,
    val evennessJ: Double,
    val simpsonD: Double,
    val dominantSpecies: String,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transect_studies")
data class TransectStudy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transectId: String,
    val type: String,
    val length: Double,
    val width: Double?,
    val startLat: Double?,
    val startLng: Double?,
    val endLat: Double?,
    val endLng: Double?,
    val surveyType: String,
    val encounterDataJson: String,
    val totalArea: Double,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gps_waypoints")
data class GpsWaypoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pointId: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "environmental_records")
data class EnvironmentalRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stationId: String,
    val dateTime: String,
    val airTemp: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDir: String,
    val par: Double,
    val soilTemp: Double,
    val soilMoisture: Double,
    val soilPh: Double,
    val soilEc: Double,
    val canopyCover: Double,
    val vpd: Double,
    val soilStatus: String,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "disturbance_records")
data class DisturbanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val date: String,
    val indicatorScoresJson: String,
    val diPercent: Double,
    val category: String,
    val photoPath: String?,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "herbarium_specimens")
data class HerbariumSpecimen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val specimenId: String,
    val family: String,
    val genus: String,
    val species: String,
    val authority: String?,
    val commonName: String,
    val date: String,
    val collector: String,
    val locality: String,
    val habitat: String,
    val habit: String,
    val parts: String,
    val photoPaths: String, // Comma separated
    val notes: String?,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

// --- MODULE 2: AGROCLIMATIC LAB ---

@Entity(tableName = "pot_experiments")
data class PotExperiment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expId: String,
    val title: String,
    val crop: String,
    val startDate: String,
    val durationWeeks: Int,
    val treatmentNamesJson: String,
    val replicates: Int,
    val potSize: Double,
    val medium: String,
    val randomisation: String,
    val wateringFreq: String,
    val parametersJson: String,
    val allocationJson: String,
    val scheduleJson: String,
    val status: String, // Active / Completed
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pot_observations")
data class PotObservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val experimentId: String,
    val date: String,
    val round: Int,
    val readingsJson: String,
    val summariesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "climate_records")
data class ClimateRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String,
    val dateTime: String,
    val tempDay: Double,
    val tempNight: Double,
    val humidity: Double,
    val co2: Double,
    val lightIntensity: Double,
    val lightHours: Double,
    val soilTemp: Double,
    val ec: Double,
    val ph: Double,
    val vpd: Double,
    val dif: Double,
    val gdd: Double,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "germination_trials")
data class GerminationTrial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trialId: String,
    val crop: String,
    val sowingDate: String,
    val seedsPerLot: Int,
    val treatmentNamesJson: String,
    val dailyCountsJson: String,
    val gpPercent: String, // JSON map
    val mgt: String, // JSON map
    val gri: String, // JSON map
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fertilizer_plans")
data class FertilizerPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crop: String,
    val stage: String,
    val area: Double,
    val areaUnit: String,
    val targetN: Double,
    val targetP: Double,
    val targetK: Double,
    val productsJson: String,
    val method: String,
    val splits: Int,
    val calculatedDosesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "irrigation_plans")
data class IrrigationPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crop: String,
    val stage: String,
    val kc: Double,
    val et0: Double,
    val rainfall: Double,
    val soilType: String,
    val rootDepth: Double,
    val efficiency: Double,
    val netRequirement: Double,
    val interval: Int,
    val volumePerEvent: Double,
    val scheduleJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pest_disease_records")
data class PestDiseaseRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crop: String,
    val date: String,
    val problemType: String,
    val name: String,
    val severity: Int,
    val incidencePercent: Double,
    val areaAffectedPercent: Double,
    val photoPaths: String?,
    val actionTaken: String,
    val followUpDate: String?,
    val dsi: Double,
    val riskLevel: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "harvest_records")
data class HarvestRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plotId: String,
    val crop: String,
    val date: String,
    val freshWeight: Double,
    val dryWeight: Double?,
    val moisturePercent: Double?,
    val yieldGm2: Double,
    val yieldKgha: Double,
    val harvestIndex: Double?,
    val qualityGrade: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "growth_records")
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: String,
    val measurementsJson: String,
    val indicesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- MODULE 3: GARDEN SCAPE ---

@Entity(tableName = "season_plans")
data class SeasonPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val zone: String,
    val season: String,
    val year: Int,
    val cropListJson: String,
    val datesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "companion_plans")
data class BedCompanionPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bedName: String,
    val cropList: String,
    val score: Double,
    val conflicts: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "soil_amendment_plans")
data class SoilAmendmentPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currentPh: Double,
    val targetPh: Double,
    val soilType: String,
    val area: Double,
    val amendmentType: String,
    val calculatedAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bed_layouts")
data class BedLayout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bedName: String,
    val shape: String,
    val dimensionsJson: String,
    val method: String,
    val spacing: Double,
    val plantCount: Int,
    val density: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "watering_schedules")
data class WateringSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crop: String,
    val stage: String,
    val netDailyNeed: Double,
    val interval: Int,
    val volume: Double,
    val scheduleJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "compost_batches")
data class CompostBatch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val batchId: String,
    val startDate: String,
    val materialsJson: String,
    val tempLogJson: String,
    val stage: String,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pest_observations")
data class PestObservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pestName: String,
    val cropAffected: String,
    val date: String,
    val level: Int,
    val photoPaths: String?,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "yield_records")
data class YieldRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bedId: String,
    val crop: String,
    val date: String,
    val harvestNum: Int,
    val area: Double,
    val totalWeight: Double,
    val marketableWeight: Double,
    val unitCount: Int,
    val avgUnitWeight: Double,
    val grade: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "yield_estimates")
data class YieldEstimate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crop: String,
    val area: Double,
    val plantCount: Int,
    val fruitsPerPlant: Double,
    val avgFruitWeight: Double,
    val adjustedYield: Double,
    val rangeLow: Double,
    val rangeHigh: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "garden_tasks")
data class GardenTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,
    val category: String,
    val notes: String?,
    val isDone: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
