package com.biologger.viewmodel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.app.Application
import androidx.lifecycle.*
import com.biologger.data.*
import kotlinx.coroutines.launch

class ScientificViewModel(application: Application) : AndroidViewModel(application) {

    val repository: ScientificRepository

    init {
        val dao = AppDatabase.getDatabase(application).scientificDao()
        repository = ScientificRepository(dao)
    }

    // --- Forest ---
    fun insertTreeMeasurement(measurement: TreeMeasurement) = viewModelScope.launch { repository.insertTreeMeasurement(measurement) }
    fun insertBasalAreaPlot(plot: BasalAreaPlot) = viewModelScope.launch { repository.insertBasalAreaPlot(plot) }
    fun insertStandDensity(result: StandDensityResult) = viewModelScope.launch { repository.insertStandDensity(result) }
    fun insertTimberVolume(volume: TimberVolume) = viewModelScope.launch { repository.insertTimberVolume(volume) }
    fun insertBiomass(biomass: BiomassResult) = viewModelScope.launch { repository.insertBiomass(biomass) }
    fun insertTreeHealth(record: TreeHealthRecord) = viewModelScope.launch { repository.insertTreeHealth(record) }
    fun insertCrownCover(record: CrownCoverRecord) = viewModelScope.launch { repository.insertCrownCover(record) }
    fun insertLitterfall(record: LitterfallRecord) = viewModelScope.launch { repository.insertLitterfall(record) }
    fun insertQuadratStudy(study: QuadratStudy) = viewModelScope.launch { repository.insertQuadratStudy(study) }
    fun insertTransectStudy(study: TransectStudy) = viewModelScope.launch { repository.insertTransectStudy(study) }
    fun insertGpsWaypoint(waypoint: GpsWaypoint) = viewModelScope.launch { repository.insertGpsWaypoint(waypoint) }
    fun insertEnvironmentalRecord(record: EnvironmentalRecord) = viewModelScope.launch { repository.insertEnvironmentalRecord(record) }
    fun insertDisturbanceRecord(record: DisturbanceRecord) = viewModelScope.launch { repository.insertDisturbanceRecord(record) }
    fun insertHerbariumSpecimen(specimen: HerbariumSpecimen) = viewModelScope.launch { repository.insertHerbariumSpecimen(specimen) }

    // --- Greenhouse ---
    val allPotExperiments: LiveData<List<PotExperiment>> = repository.getAllPotExperiments()

    fun insertPotExperiment(exp: PotExperiment) = viewModelScope.launch { repository.insertPotExperiment(exp) }
    fun insertPotObservation(obs: PotObservation) = viewModelScope.launch { repository.insertPotObservation(obs) }
    fun insertClimateRecord(record: ClimateRecord) = viewModelScope.launch { repository.insertClimateRecord(record) }
    fun insertGerminationTrial(trial: GerminationTrial) = viewModelScope.launch { repository.insertGerminationTrial(trial) }
    fun insertFertilizerPlan(plan: FertilizerPlan) = viewModelScope.launch { repository.insertFertilizerPlan(plan) }
    fun insertIrrigationPlan(plan: IrrigationPlan) = viewModelScope.launch { repository.insertIrrigationPlan(plan) }
    fun insertPestDiseaseRecord(record: PestDiseaseRecord) = viewModelScope.launch { repository.insertPestDiseaseRecord(record) }
    fun insertHarvestRecord(record: HarvestRecord) = viewModelScope.launch { repository.insertHarvestRecord(record) }
    fun insertGrowthRecord(record: GrowthRecord) = viewModelScope.launch { repository.insertGrowthRecord(record) }

    // --- Garden ---
    fun insertSeasonPlan(plan: SeasonPlan) = viewModelScope.launch { repository.insertSeasonPlan(plan) }
    fun insertBedCompanionPlan(plan: BedCompanionPlan) = viewModelScope.launch { repository.insertBedCompanionPlan(plan) }
    fun insertSoilAmendmentPlan(plan: SoilAmendmentPlan) = viewModelScope.launch { repository.insertSoilAmendmentPlan(plan) }
    fun insertBedLayout(layout: BedLayout) = viewModelScope.launch { repository.insertBedLayout(layout) }
    fun insertWateringSchedule(schedule: WateringSchedule) = viewModelScope.launch { repository.insertWateringSchedule(schedule) }
    fun insertCompostBatch(batch: CompostBatch) = viewModelScope.launch { repository.insertCompostBatch(batch) }
    fun insertPestObservation(obs: PestObservation) = viewModelScope.launch { repository.insertPestObservation(obs) }
    fun insertYieldRecord(record: YieldRecord) = viewModelScope.launch { repository.insertYieldRecord(record) }
    fun insertYieldEstimate(estimate: YieldEstimate) = viewModelScope.launch { repository.insertYieldEstimate(estimate) }
    fun insertGardenTask(task: GardenTask) = viewModelScope.launch { repository.insertGardenTask(task) }
}
