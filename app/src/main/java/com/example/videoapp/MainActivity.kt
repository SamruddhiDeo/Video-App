package com.example.videoapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    companion object{
   public lateinit var videoRecyclerView : RecyclerView
    lateinit var searchView : SearchView

    }
    var arrVideos = ArrayList<VideosModel>()
    var filteredVideo : List<VideosModel> = emptyList()
    var isSearching : Boolean = false
    var searchSelected : Boolean = false
    var modifyList : Boolean = true
    var firstTimeLoad : Boolean = true

    private lateinit var supabaseClient: SupabaseClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById<SearchView>(R.id.searchView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        videoRecyclerView = findViewById<RecyclerView>(R.id.videoRecyclerView)

        setSupportActionBar(toolbar)

        // Initialize Supabase client
        val supabaseUrl = "https://khsbxdrehuszmpziipot.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtoc2J4ZHJlaHVzem1wemlpcG90Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUzMjgyNDQsImV4cCI6MjAzMDkwNDI0NH0.McMt_xBBmd0eyLET5wJdPGZuWpi3-d-R8vLXN16SmIM"
        supabaseClient = createSupabaseClient(supabaseUrl,supabaseKey){
            install(Postgrest)
            defaultSerializer = KotlinXSerializer(Json {
                //apply your custom config
            })
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }


            override fun onQueryTextChange(newText: String): Boolean {

                if(searchView.query.toString().isEmpty()){
                    searchSelected = false;
                    modifyList = true
                }else{
                    searchSelected = true;
                    modifyList=false
                }
                // Handle text changes if needed
                fetchDataFromTable();
                performSearch(searchView.query.toString())
                return true
            }
        })



       fetchDataFromTable();
    }

    fun fetchDataFromTable() : ArrayList<VideosModel> {
        lifecycleScope.launch{
//            val response = supabaseClient.from("video_data").select()

           val response = supabaseClient.postgrest["video_data"].select()
            val data = response.decodeList<Videos>()

            if(searchSelected){
                arrVideos = ArrayList(filteredVideo);
                videoRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                var recyclerVideosAdapter = RecyclerVideosAdapter(applicationContext,arrVideos)
                videoRecyclerView.adapter = recyclerVideosAdapter


            }else if(modifyList){
                arrVideos.clear()
                for (entry in data){
                    Log.d("checkbug","inside");
                    val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video1)
                    arrVideos.add(VideosModel(R.drawable.channel_logo1,entry.title,entry.channel_name,videoUri));
                }

                if(firstTimeLoad){
                    filteredVideo = arrVideos
                    firstTimeLoad = false
                }

                videoRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                var recyclerVideosAdapter = RecyclerVideosAdapter(applicationContext,arrVideos)
                videoRecyclerView.adapter = recyclerVideosAdapter

modifyList = false
            }

        }
        return arrVideos;

    }

    private fun performSearch(query: String) {
        filteredVideo = arrVideos.filter { videosModel ->
            videosModel.videoTitle.toLowerCase().contains(query.toLowerCase())
        }
        modifyList = true
fetchDataFromTable()
    }

}

@Serializable
data class Videos(
    val id : Int =0,
    val title : String = "",
    val channel_name : String = ""
)