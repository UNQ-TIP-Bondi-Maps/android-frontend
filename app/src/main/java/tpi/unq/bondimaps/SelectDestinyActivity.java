package tpi.unq.bondimaps;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectDestinyActivity extends ListActivity {

    public ServiceManager serviceManager;
    public String serverIp;
    public int linesToAdd;
    public List<String> lines;
    public List<String> linesIds;
    private SeekBar blocksBar;
    private TextView blocksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_destiny);

        blocksBar = (SeekBar) findViewById(R.id.seek_bar_blocks);
        blocksText = (TextView) findViewById(R.id.text_view_blocks);
        serviceManager = new ServiceManager(this);
        lines = new ArrayList<>();
        linesIds = new ArrayList<>();
        serverIp = (String) getIntent().getExtras().get("ipBack");

        blocksText.setText("Cuadras a caminar: " + blocksBar.getProgress() + "/" + blocksBar.getMax());

        blocksBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Cuadras a caminar en destino", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blocksText.setText("Covered: " + progress + "/" + seekBar.getMax());
                Toast.makeText(getApplicationContext(), "Presione botón de búsqueda para filtrar", Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("Lines request: ", "Start lines request");
        String url = "http://" + serverIp+ ":8080/backend/rest/busLines/list";

        JSONArray linesArray;

        try {
            linesArray = serviceManager.getListResource(url);
            linesToAdd = linesArray.length();
            for(int i=0; i<this.linesToAdd; i++) {
                JSONObject aLine = linesArray.getJSONObject(i);
                int numLine = aLine.getInt("line");
                String idLine = String.valueOf(aLine.getInt("id"));
                Log.i("A bus line ", " - id: " + idLine + " numLine: " + numLine);
                lines.add("Línea " + numLine);
               // linesIds.add(idLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView listview= getListView();
        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);

        listview.setTextFilterEnabled(true);

        setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_checked,lines.toArray()));

        FloatingActionButton myLinesButton = (FloatingActionButton) findViewById(R.id.confirm_buses_list);
        myLinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectDestinyActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                for(int i = 0; i < linesIds.size(); i++) {
                    intent.putExtra("line"+i, linesIds.get(i));
                }
                intent.putExtra("linesSize", linesIds.size());
                intent.putExtra("ipBack", serverIp);
                startActivity(intent);
            }
        });
    }

    public void onListItemClick(ListView parent, View v,int position,long id){
        CheckedTextView item = (CheckedTextView) v;
        String lineNumber = lines.get(position).substring(6);
        if(item.isChecked()) {
            linesIds.add(lineNumber);
        }
        else{
            if(linesIds.contains(lineNumber)) {
                linesIds.remove(lineNumber);
            }
        }
        Toast.makeText(this, lines.toArray()[position] + " checked : " +
                item.isChecked(), Toast.LENGTH_SHORT).show();
    }

}






