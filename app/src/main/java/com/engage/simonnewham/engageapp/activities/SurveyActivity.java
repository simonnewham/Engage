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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.Question;
import com.engage.simonnewham.engageapp.models.Survey;
import com.engage.simonnewham.engageapp.models.SurveyResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {

    //extra info on user
    String email;
    String surveyID = null;
    String user_group;

    private final String TAG = "SurveyActivity";
    LinearLayout lPanel;
    int current =-1; //int to keep track of question number being displayed
    Survey survey; //current survey object
    ArrayList<String> responses;

    Button begin;
    Button submit;
    Button next;
    RadioGroup radioGroup;
    EditText editText;
    TextView questionTitle;
    ImageView survey_tick;

    //temporary views for begin survey
    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Array of answers
        responses = new ArrayList<>();

        //View elements
        lPanel = (LinearLayout) findViewById(R.id.survey_panel);
        begin = (Button) findViewById(R.id.button_begin);
        submit = (Button) findViewById(R.id.button_submit);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);
        editText = (EditText) findViewById(R.id.editText);
        questionTitle = (TextView) findViewById(R.id.text_question);
        next = (Button) findViewById(R.id.button_next);
        survey_tick = (ImageView) findViewById(R.id.imageView);

        //get extra info
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            surveyID = extras.getString("surveyID");
        }

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Survey");
        setSupportActionBar(toolbar);

        loadSurvey(surveyID);
    }

    /**
     * Method to load survey
     * @param surveyID
     */
    public void loadSurvey(String surveyID){

        //download survey from DB, http request will depend on surveyID


        //set up Question model
        ArrayList<Question> questionList = new ArrayList<>(); //hold all questions in survey

        try{
            JSONObject reader = new JSONObject(loadJSONFromAsset(surveyID));

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

    //displays the initial survey title and description
    public void displaySurvey(Survey s){

        if(surveyID.equals("Baseline.json")){
            TextView thank = new TextView(this);
            thank.setText("Welcome "+email+"!"+"\n"+"\nThank you for signing up for ENGAGE, please complete the following questionnaire to finish the sign up process! \n");
            thank.setTypeface(null, Typeface.BOLD);
            thank.setTextSize(20);
            lPanel.addView(thank);
        }

        title = new TextView(this);
        title.setText("Survey Title: "+s.getTitle());
        title.setTypeface(null, Typeface.BOLD);
        title.setTextSize(15);
        lPanel.addView(title);

        description = new TextView(this);
        description.setText("\n"+s.getDescription());
        description.setTextSize(15);
        lPanel.addView(description);

    }
    public void onBegin(View view){
        begin.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        questionTitle.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        onNext();

    }

    //onClick listener for when next button is pressed
    //checks if user has supplied input to question before allowing user to answer next question
    public void checkInput(View view){

        //code to hide keyboard after each question
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }

        //add current response to responses before allow user to go to next question
        String response;
        if(editText.getVisibility() == View.VISIBLE ){
            response = editText.getText().toString();
            if (!response.equals("")){
                responses.add(current, response);
                onNext();
            }
            else{
                Toast.makeText(this, "Please input a response", Toast.LENGTH_SHORT).show();
            }

        }
        else if(radioGroup.getVisibility() == View.VISIBLE){

            if(radioGroup.getCheckedRadioButtonId() != -1){
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                response = radioButton.getText().toString();
                responses.add(current, response);
                onNext();
            }
            else{
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            }
        }

        if(current==survey.getqNum()){
            //lPanel.removeAllViews();
            //remove all options after last question
            submit.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
            survey_tick.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            radioGroup.removeAllViews();
            radioGroup.clearCheck();
            radioGroup.setVisibility(View.GONE);
            questionTitle.setVisibility(View.GONE);

            //TRACING
            SurveyResponse surveyResponse = new SurveyResponse(email, user_group, surveyID, responses );
            Gson gson = new Gson();
            String json = gson.toJson(surveyResponse);
            TextView test = new TextView(this);
            test.setText(json);
            lPanel.addView(test);
        }
    }

    /**
     * Method to populate the next question
     *
     */
    public void onNext(){

        //stop keyboard from showing
        current = (current+1); //for final question it will make current equal to question number

        if( current < survey.getqNum()){
            //lPanel.removeAllViews();
            editText.setText("");
            editText.setVisibility(View.GONE);
            radioGroup.removeAllViews();
            radioGroup.clearCheck();
            radioGroup.setVisibility(View.GONE);

            Question toLoad = survey.getQuestions().get(current);

            int temp = current+1;
            //TextView q = new TextView(this);
            questionTitle.setText("Question "+temp+": "+toLoad.getQuestion());

            String type = toLoad.getType();

            //add option to input response
            if(type.equals("Text")){
                editText.setVisibility(View.VISIBLE);
            }

            //add radio buttons for each option
            else if (type.equals("MCQ")){
                //RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setVisibility(View.VISIBLE);
                ArrayList<String> options = toLoad.getOptions();

                for(int i=0; i<options.size();i++){
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(options.get(i));
                    radioGroup.addView(radioButton);
                }
                //lPanel.addView(radioGroup);
            }
        }
    }

    /**
     * onClick listener for when submit is clicked
     * Converts survey to JSON format and pushes document to DB
     * @param view
     */
    public void onSubmit(View view){
        Toast.makeText(this, "Survey Completed", Toast.LENGTH_SHORT).show();
        //load survey complete image


        //create survey resonse object and convert to JSON representation
        SurveyResponse surveyResponse = new SurveyResponse(email, user_group, surveyID, responses );
        Gson gson = new Gson();
        String json = gson.toJson(surveyResponse);

        Log.i(TAG, ">>>>>>>JSON SURVEY RESPONSE<<<<<<<"+json);

        //take user to user home page keeping track of email and group
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        startActivity(intent);
        finish();
    }

    /**
     * Method to convert a JSON file into a string
     * @return
     */
    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
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


    //Method for setting up toolbar options
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
                finish();
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
