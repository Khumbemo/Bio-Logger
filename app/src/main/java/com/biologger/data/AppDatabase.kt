package com.biologger.data
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Note::class,
        TreeMeasurement::class,
        BasalAreaPlot::class,
        StandDensityResult::class,
        TimberVolume::class,
        BiomassResult::class,
        TreeHealthRecord::class,
        CrownCoverRecord::class,
        LitterfallRecord::class,
        QuadratStudy::class,
        TransectStudy::class,
        GpsWaypoint::class,
        EnvironmentalRecord::class,
        DisturbanceRecord::class,
        HerbariumSpecimen::class,
        PotExperiment::class,
        PotObservation::class,
        ClimateRecord::class,
        GerminationTrial::class,
        FertilizerPlan::class,
        IrrigationPlan::class,
        PestDiseaseRecord::class,
        HarvestRecord::class,
        GrowthRecord::class,
        SeasonPlan::class,
        BedCompanionPlan::class,
        SoilAmendmentPlan::class,
        BedLayout::class,
        WateringSchedule::class,
        CompostBatch::class,
        PestObservation::class,
        YieldRecord::class,
        YieldEstimate::class,
        GardenTask::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun scientificDao(): ScientificDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biologger_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
