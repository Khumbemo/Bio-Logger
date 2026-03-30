package com.biologger.data
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.os.Bundle

import androidx.lifecycle.LiveData

class ScientificRepository(private val dao: ScientificDao) {

    // Forest
    suspend fun insertTreeMeasurement(measurement: TreeMeasurement) = dao.insertTreeMeasurement(measurement)
    fun getAllTreeMeasurements() = dao.getAllTreeMeasurements()

    suspend fun insertBasalAreaPlot(plot: BasalAreaPlot) = dao.insertBasalAreaPlot(plot)
    fun getAllBasalAreaPlots() = dao.getAllBasalAreaPlots()

    suspend fun insertStandDensity(result: StandDensityResult) = dao.insertStandDensity(result)
    fun getAllStandDensityResults() = dao.getAllStandDensityResults()

    suspend fun insertTimberVolume(volume: TimberVolume) = dao.insertTimberVolume(volume)
    fun getAllTimberVolumes() = dao.getAllTimberVolumes()

    suspend fun insertBiomass(biomass: BiomassResult) = dao.insertBiomass(biomass)
    fun getAllBiomassResults() = dao.getAllBiomassResults()

    suspend fun insertTreeHealth(record: TreeHealthRecord) = dao.insertTreeHealth(record)
    fun getAllTreeHealthRecords() = dao.getAllTreeHealthRecords()

    suspend fun insertCrownCover(record: CrownCoverRecord) = dao.insertCrownCover(record)
    fun getAllCrownCoverRecords() = dao.getAllCrownCoverRecords()

    suspend fun insertLitterfall(record: LitterfallRecord) = dao.insertLitterfall(record)
    fun getAllLitterfallRecords() = dao.getAllLitterfallRecords()

    suspend fun insertQuadratStudy(study: QuadratStudy) = dao.insertQuadratStudy(study)
    fun getAllQuadratStudies() = dao.getAllQuadratStudies()

    suspend fun insertTransectStudy(study: TransectStudy) = dao.insertTransectStudy(study)
    fun getAllTransectStudies() = dao.getAllTransectStudies()

    suspend fun insertGpsWaypoint(waypoint: GpsWaypoint) = dao.insertGpsWaypoint(waypoint)
    fun getAllGpsWaypoints() = dao.getAllGpsWaypoints()

    suspend fun insertEnvironmentalRecord(record: EnvironmentalRecord) = dao.insertEnvironmentalRecord(record)
    fun getAllEnvironmentalRecords() = dao.getAllEnvironmentalRecords()

    suspend fun insertDisturbanceRecord(record: DisturbanceRecord) = dao.insertDisturbanceRecord(record)
    fun getAllDisturbanceRecords() = dao.getAllDisturbanceRecords()

    suspend fun insertHerbariumSpecimen(specimen: HerbariumSpecimen) = dao.insertHerbariumSpecimen(specimen)
    fun getAllHerbariumSpecimens() = dao.getAllHerbariumSpecimens()

    // Greenhouse
    suspend fun insertPotExperiment(exp: PotExperiment) = dao.insertPotExperiment(exp)
    fun getAllPotExperiments() = dao.getAllPotExperiments()

    suspend fun insertPotObservation(obs: PotObservation) = dao.insertPotObservation(obs)
    fun getObservationsForExperiment(expId: String) = dao.getObservationsForExperiment(expId)

    suspend fun insertClimateRecord(record: ClimateRecord) = dao.insertClimateRecord(record)
    fun getAllClimateRecords() = dao.getAllClimateRecords()

    suspend fun insertGerminationTrial(trial: GerminationTrial) = dao.insertGerminationTrial(trial)
    fun getAllGerminationTrials() = dao.getAllGerminationTrials()

    suspend fun insertFertilizerPlan(plan: FertilizerPlan) = dao.insertFertilizerPlan(plan)
    fun getAllFertilizerPlans() = dao.getAllFertilizerPlans()

    suspend fun insertIrrigationPlan(plan: IrrigationPlan) = dao.insertIrrigationPlan(plan)
    fun getAllIrrigationPlans() = dao.getAllIrrigationPlans()

    suspend fun insertPestDiseaseRecord(record: PestDiseaseRecord) = dao.insertPestDiseaseRecord(record)
    fun getAllPestDiseaseRecords() = dao.getAllPestDiseaseRecords()

    suspend fun insertHarvestRecord(record: HarvestRecord) = dao.insertHarvestRecord(record)
    fun getAllHarvestRecords() = dao.getAllHarvestRecords()

    suspend fun insertGrowthRecord(record: GrowthRecord) = dao.insertGrowthRecord(record)
    fun getAllGrowthRecords() = dao.getAllGrowthRecords()

    // Garden
    suspend fun insertSeasonPlan(plan: SeasonPlan) = dao.insertSeasonPlan(plan)
    fun getAllSeasonPlans() = dao.getAllSeasonPlans()

    suspend fun insertBedCompanionPlan(plan: BedCompanionPlan) = dao.insertBedCompanionPlan(plan)
    fun getAllBedCompanionPlans() = dao.getAllBedCompanionPlans()

    suspend fun insertSoilAmendmentPlan(plan: SoilAmendmentPlan) = dao.insertSoilAmendmentPlan(plan)
    fun getAllSoilAmendmentPlans() = dao.getAllSoilAmendmentPlans()

    suspend fun insertBedLayout(layout: BedLayout) = dao.insertBedLayout(layout)
    fun getAllBedLayouts() = dao.getAllBedLayouts()

    suspend fun insertWateringSchedule(schedule: WateringSchedule) = dao.insertWateringSchedule(schedule)
    fun getAllWateringSchedules() = dao.getAllWateringSchedules()

    suspend fun insertCompostBatch(batch: CompostBatch) = dao.insertCompostBatch(batch)
    fun getAllCompostBatches() = dao.getAllCompostBatches()

    suspend fun insertPestObservation(obs: PestObservation) = dao.insertPestObservation(obs)
    fun getAllPestObservations() = dao.getAllPestObservations()

    suspend fun insertYieldRecord(record: YieldRecord) = dao.insertYieldRecord(record)
    fun getAllYieldRecords() = dao.getAllYieldRecords()
    fun getCumulativeYield(crop: String, bedId: String) = dao.getCumulativeYield(crop, bedId)

    suspend fun insertYieldEstimate(estimate: YieldEstimate) = dao.insertYieldEstimate(estimate)
    fun getAllYieldEstimates() = dao.getAllYieldEstimates()

    suspend fun insertGardenTask(task: GardenTask) = dao.insertGardenTask(task)
    fun getAllGardenTasks() = dao.getAllGardenTasks()
}
