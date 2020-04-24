package com.example.map19roomintro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    //By implementing CoroutineScope, our mainActivity is CouroutineScope
    private lateinit var job : Job
    //Courotinescope should have courotineScope
    override val coroutineContext : CoroutineContext
    get() = Dispatchers.Main + job

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext,
            AppDatabase::class.java,"shopping-items")
            .fallbackToDestructiveMigration().build()

        val item1 = Item(0, "Ost", false, "Kyl" )
        val item2 = Item(0, "Smör", false, "Kyl" )
        val item3 = Item(0, "Bönor", false, "Grönsak" )
        /*saveItem(item1)
        saveItem(item2)
        saveItem(item3)*/

        var items = loadAllItems()

        var itemsByCat = loadByCategory("Köt")

        launch {
            itemsByCat.await().forEach{
                println("!!! ${it.category}")
            }
        }
    }

    fun saveItem(item: Item): Unit {
        //GlobalScope is used to run our DB on another thread rather then main thread
        //Dispatchers.IO means lower priority
        async(Dispatchers.IO) {
            db.itemDao().insert(item)
        }
    }
    //Defered is list of items which are not done yet
    fun loadAllItems(): Deferred<List<Item>> =
         async(Dispatchers.IO) {
                db.itemDao().getAll()
            }

    fun loadByCategory(category: String): Deferred<List<Item>> {
        return async(Dispatchers.IO) {
            db.itemDao().findByCategory(category)
        }
    }
}
