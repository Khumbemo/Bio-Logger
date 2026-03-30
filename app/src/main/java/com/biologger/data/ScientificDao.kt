package com.biologger.data
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.os.Bundle

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScientificDao {

    // Tree Measurement
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreeMeasurement(measurement: TreeMeasurement)

    @Query("SELECT * FROM tree_measurements ORDER BY timestamp DESC")
    fun getAllTreeMeasurements(): LiveData<List<TreeMeasurement>>

    // Basal Area
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasalAreaPlot(plot: BasalAreaPlot)

    @Query("SELECT * FROM basal_area_plots ORDER BY timestamp DESC")
    fun getAllBasalAreaPlots(): LiveData<List<BasalAreaPlot>>

    // Stand Density
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandDensity(result: StandDensityResult)

    @Query("SELECT * FROM stand_density ORDER BY timestamp DESC")
    fun getAllStandDensityResults(): LiveData<List<StandDensityResult>>

    // Timber Volume
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimberVolume(volume: TimberVolume)

    @Query("SELECT * FROM timber_volume ORDER BY timestamp DESC")
    fun getAllTimberVolumes(): LiveData<List<TimberVolume>>

    // Biomass
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiomass(biomass: BiomassResult)

    @Query("SELECT * FROM biomass_results ORDER BY timestamp DESC")
    fun getAllBiomassResults(): LiveData<List<BiomassResult>>

    // Tree Health
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreeHealth(record: TreeHealthRecord)

    @Query("SELECT * FROM tree_health ORDER BY timestamp DESC")
    fun getAllTreeHealthRecords(): LiveData<List<TreeHealthRecord>>

    // Crown Cover
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrownCover(record: CrownCoverRecord)

    @Query("SELECT * FROM crown_cover ORDER BY timestamp DESC")
    fun getAllCrownCoverRecords(): LiveData<List<CrownCoverRecord>>

    // Litterfall
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLitterfall(record: LitterfallRecord)

    @Query("SELECT * FROM litterfall ORDER BY timestamp DESC")
    fun getAllLitterfallRecords(): LiveData<List<LitterfallRecord>>

    // Quadrat Studies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuadratStudy(study: QuadratStudy)

    @Query("SELECT * FROM quadrat_studies ORDER BY timestamp DESC")
    fun getAllQuadratStudies(): LiveData<List<QuadratStudy>>

    // Transect Studies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransectStudy(study: TransectStudy)

    @Query("SELECT * FROM transect_studies ORDER BY timestamp DESC")
    fun getAllTransectStudies(): LiveData<List<TransectStudy>>

    // GPS Waypoints
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsWaypoint(waypoint: GpsWaypoint)

    @Query("SELECT * FROM gps_waypoints ORDER BY timestamp DESC")
    fun getAllGpsWaypoints(): LiveData<List<GpsWaypoint>>

    // Environmental Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironmentalRecord(record: EnvironmentalRecord)

    @Query("SELECT * FROM environmental_records ORDER BY timestamp DESC")
    fun getAllEnvironmentalRecords(): LiveData<List<EnvironmentalRecord>>

    // Disturbance Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisturbanceRecord(record: DisturbanceRecord)

    @Query("SELECT * FROM disturbance_records ORDER BY timestamp DESC")
    fun getAllDisturbanceRecords(): LiveData<List<DisturbanceRecord>>

    // Herbarium specimens
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHerbariumSpecimen(specimen: HerbariumSpecimen)

    @Query("SELECT * FROM herbarium_specimens ORDER BY timestamp DESC")
    fun getAllHerbariumSpecimens(): LiveData<List<HerbariumSpecimen>>

    // Pot Experiments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPotExperiment(exp: PotExperiment)

    @Query("SELECT * FROM pot_experiments ORDER BY timestamp DESC")
    fun getAllPotExperiments(): LiveData<List<PotExperiment>>

    // Pot Observations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPotObservation(obs: PotObservation)

    @Query("SELECT * FROM pot_observations WHERE experimentId = :expId ORDER BY timestamp DESC")
    fun getObservationsForExperiment(expId: String): LiveData<List<PotObservation>>

    // Climate Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClimateRecord(record: ClimateRecord)

    @Query("SELECT * FROM climate_records ORDER BY timestamp DESC")
    fun getAllClimateRecords(): LiveData<List<ClimateRecord>>

    // Germination Trials
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGerminationTrial(trial: GerminationTrial)

    @Query("SELECT * FROM germination_trials ORDER BY timestamp DESC")
    fun getAllGerminationTrials(): LiveData<List<GerminationTrial>>

    // Fertilizer Plans
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFertilizerPlan(plan: FertilizerPlan)

    @Query("SELECT * FROM fertilizer_plans ORDER BY timestamp DESC")
    fun getAllFertilizerPlans(): LiveData<List<FertilizerPlan>>

    // Irrigation Plans
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIrrigationPlan(plan: IrrigationPlan)

    @Query("SELECT * FROM irrigation_plans ORDER BY timestamp DESC")
    fun getAllIrrigationPlans(): LiveData<List<IrrigationPlan>>

    // Pest Disease Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPestDiseaseRecord(record: PestDiseaseRecord)

    @Query("SELECT * FROM pest_disease_records ORDER BY timestamp DESC")
    fun getAllPestDiseaseRecords(): LiveData<List<PestDiseaseRecord>>

    // Harvest Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvestRecord(record: HarvestRecord)

    @Query("SELECT * FROM harvest_records ORDER BY timestamp DESC")
    fun getAllHarvestRecords(): LiveData<List<HarvestRecord>>

    // Growth Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecord)

    @Query("SELECT * FROM growth_records ORDER BY timestamp DESC")
    fun getAllGrowthRecords(): LiveData<List<GrowthRecord>>

    // Season Plans
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeasonPlan(plan: SeasonPlan)

    @Query("SELECT * FROM season_plans ORDER BY timestamp DESC")
    fun getAllSeasonPlans(): LiveData<List<SeasonPlan>>

    // Companion Plans
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBedCompanionPlan(plan: BedCompanionPlan)

    @Query("SELECT * FROM companion_plans ORDER BY timestamp DESC")
    fun getAllBedCompanionPlans(): LiveData<List<BedCompanionPlan>>

    // Soil Amendment Plans
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilAmendmentPlan(plan: SoilAmendmentPlan)

    @Query("SELECT * FROM soil_amendment_plans ORDER BY timestamp DESC")
    fun getAllSoilAmendmentPlans(): LiveData<List<SoilAmendmentPlan>>

    // Bed Layouts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBedLayout(layout: BedLayout)

    @Query("SELECT * FROM bed_layouts ORDER BY timestamp DESC")
    fun getAllBedLayouts(): LiveData<List<BedLayout>>

    // Watering Schedules
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWateringSchedule(schedule: WateringSchedule)

    @Query("SELECT * FROM watering_schedules ORDER BY timestamp DESC")
    fun getAllWateringSchedules(): LiveData<List<WateringSchedule>>

    // Compost Batches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompostBatch(batch: CompostBatch)

    @Query("SELECT * FROM compost_batches ORDER BY timestamp DESC")
    fun getAllCompostBatches(): LiveData<List<CompostBatch>>

    // Pest Observations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPestObservation(obs: PestObservation)

    @Query("SELECT * FROM pest_observations ORDER BY timestamp DESC")
    fun getAllPestObservations(): LiveData<List<PestObservation>>

    // Yield Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYieldRecord(record: YieldRecord)

    @Query("SELECT * FROM yield_records ORDER BY timestamp DESC")
    fun getAllYieldRecords(): LiveData<List<YieldRecord>>

    @Query("SELECT SUM(totalWeight) FROM yield_records WHERE crop = :crop AND bedId = :bedId")
    fun getCumulativeYield(crop: String, bedId: String): LiveData<Double>

    // Yield Estimates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYieldEstimate(estimate: YieldEstimate)

    @Query("SELECT * FROM yield_estimates ORDER BY timestamp DESC")
    fun getAllYieldEstimates(): LiveData<List<YieldEstimate>>

    // Garden Tasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGardenTask(task: GardenTask)

    @Query("SELECT * FROM garden_tasks ORDER BY date ASC")
    fun getAllGardenTasks(): LiveData<List<GardenTask>>
}
