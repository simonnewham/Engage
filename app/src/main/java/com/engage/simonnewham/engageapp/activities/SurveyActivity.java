package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.Question;
import com.engage.simonnewham.engageapp.models.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {

    String surveyID = null;
    private final String TAG = "SurveyActivity";
    LinearLayout lPanel;
    int current =-1; //int to keep track of question number being displayed
    Survey survey;
    ArrayList<String> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        lPanel = (LinearLayout) findViewById(R.id.survey_panel);
        responses = new ArrayList<>();

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Survey");
        setSupportActionBar(toolbar);

        //get extra info
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            surveyID = extras.getString("surveyID"); //baseline

        loadSurvey(surveyID);
    }

    /**
     * Method to load survey
     * @param surveyID
     */
    public void loadSurvey(String surveyID){

        //download survey from DB



        //set up Question model
        ArrayList<Question> questionList = new ArrayList<>(); //hold all questions in survey

        try{
            JSONObject reader = new JSONObject(loadJSONFromAsset());

            //JSONObject jTitle = reader.getJSONObject("Title");
           // jTitle = reader.getString("Title");
            int q = reader.getInt("Qnumber"); //the number of questions
            Log.i(TAG, "NUMBER OF QUESTIONS>>>>>>>>"+q);
            //iterate over questions and create new question objects
            for(int i=1; i<q+1; i++){

                JSONObject jQuestion = reader.getJSONObject("Q"+i);
                Question question=null;

                if(jQuestion.getString("Type").equals("Text")){
                    question = new Question(jQuestion.getString("Question"),jQuestion.getString("Type"));
                }

                else if (jQuestion.getString("Type").equals("MCQ")){
                    ArrayList<String> options = new ArrayList<>();

                    JSONArray jOptions = jQuestion.getJSONArray("Options");
                    if(jOptions!=null){
                        for (int k=0; k<jOptions.length(); k++){
                            String option = jOptions.getString(k);
                            options.add(option);

                        }
                    }

                    question = new Question(jQuestion.getString("Question"),jQuestion.getString("Type"), options);

                }
                if(question !=null){
                    questionList.add(question);
                }

           }

//            for (int i=0; i<questionList.size(); i++){
//                Log.i(TAG, "QUESTION LIST OUTPUT>>>>>>>>"+questionList.get(i).toString());
//            }

            //create Survey object containing question list
            if(questionList !=null){
                survey = new Survey(reader.getString("Survey_ID"), reader.getString("Title"), reader.getString("Description"),reader.getString("News_ID"),
                        reader.getString("Date_Created"),reader.getInt("Qnumber"),questionList);

                displaySurvey(survey);

            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        //Display survey in activity_survey based on question type

    }

    public void displaySurvey(Survey s){

        TextView title = new TextView(this);
        title.setText("Survey Title: "+s.getTitle());
        title.setTypeface(null, Typeface.BOLD);
        lPanel.addView(title);
        TextView description = new TextView(this);
        description.setText("Info:"+"\n"+s.getDescription());
        lPanel.addView(description);

    }

    public void checkInput(View view){

        //add current response to responses before allow user to go to next question
        String response;


        onNext();
    }
    /**
     * Method to handle when the next button is pressed on a survey
     *
     */
    public void onNext(){

        //check previous content


        lPanel.removeAllViews();

        current = (current+1)%survey.getqNum();
        Question toLoad = survey.getQuestions().get(current);

        int temp = current+1;
        TextView q = new TextView(this);
        q.setText("Question "+temp+": "+toLoad.getQuestion());
        q.setTypeface(null, Typeface.BOLD);
        lPanel.addView(q);

        String type = toLoad.getType();

        //add option to input response
        if(type.equals("Text")){
            EditText response = new EditText(this);
            lPanel.addView(response);
        }

        //add radio buttons for each option
        else if (type.equals("MCQ")){
            RadioGroup radioGroup = new RadioGroup(this);
            ArrayList<String> options = toLoad.getOptions();

            for(int i=0; i<options.size();i++){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(options.get(i));
                radioGroup.addView(radioButton);
            }
            lPanel.addView(radioGroup);
        }

    }

    /**
     * Method to handle when the previous button is pressed on a survey
     * @param view
     */
    public void onPrevious(View view){

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("baseline.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    //Method for setting up toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(SurveyActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(SurveyActivity.this, SignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
