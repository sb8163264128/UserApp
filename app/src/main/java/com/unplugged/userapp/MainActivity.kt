package com.unplugged.userapp

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unplugged.data.ipc.InterAppContracts.DATA_APP_DEVICE_LIST_ACTIVITY
import com.unplugged.data.ipc.InterAppContracts.DATA_APP_PACKAGE_NAME
import com.unplugged.data.ipc.InterAppContracts.EXTRA_SEARCH_QUERY
import com.unplugged.userapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearch()
    }

    private fun setupSearch() {
        binding.buttonSearch.setOnClickListener {
            launchDeviceListActivity()
            hideKeyboard()
        }
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                launchDeviceListActivity()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    private fun launchDeviceListActivity() {
        val query = binding.editTextSearch.text.toString()
        val intent = Intent().apply {
            component = ComponentName(DATA_APP_PACKAGE_NAME, DATA_APP_DEVICE_LIST_ACTIVITY)
            putExtra(EXTRA_SEARCH_QUERY, query)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "DataApp not found. Please install DataApp.", Toast.LENGTH_LONG)
                .show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied to access DataApp.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Could not launch DataApp: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}