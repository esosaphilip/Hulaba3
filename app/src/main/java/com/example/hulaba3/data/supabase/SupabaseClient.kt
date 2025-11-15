package com.example.hulaba3.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
 
class SupabaseClient {
    
    companion object {
        private const val SUPABASE_URL = "https://your-project.supabase.co" // Replace with actual URL
        private const val SUPABASE_ANON_KEY = "your-anon-key" // Replace with actual anon key
        private const val SUPABASE_SERVICE_KEY = "your-service-key" // Replace with actual service key
    }
    
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
            install(Functions)
        }
    }
    
    val auth: Auth get() = client.auth
    val postgrest: Postgrest get() = client.postgrest
    val realtime: Realtime get() = client.realtime
    val storage: Storage get() = client.storage
    val functions: Functions get() = client.functions
}