package org.mcxa.vortaro

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // tag for log methods
    val TAG = "Main_Activity"
    var dbHelper: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up the action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        dbHelper = DatabaseHelper(this)

        word_view.apply{
            adapter = WordAdapter()
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        search_text.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val w = word_view.adapter as WordAdapter
                if (!s.isNullOrEmpty()) {
                    dbHelper?.search(s.toString().toLowerCase(), w)
                } else {
                    w.words.beginBatchedUpdates()
                    // remove items at end, to avoid unnecessary array shifting
                    while (w.words.size() > 0) {
                        w.words.removeItemAt(w.words.size() - 1)
                    }
                    w.words.endBatchedUpdates()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.about -> {
                val i = Intent(this, AboutActivity::class.java)
                startActivity(i) // brings up the second activity
                return true
            }
        }

        return false
    }

    override fun onDestroy() {
        dbHelper?.close()
        super.onDestroy()
    }

    private fun handleIntent(intent: Intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (intent.action == Intent.ACTION_PROCESS_TEXT) {
                val text = intent.getCharSequenceExtra(android.content.Intent.EXTRA_PROCESS_TEXT)
                if (!text.isNullOrEmpty()) {
                    val w = word_view.adapter as WordAdapter
                    dbHelper?.search(text.toString().toLowerCase(), w)
                }
            }
        }
    }

}