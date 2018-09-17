package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.models.NewsItem;
import com.engage.simonnewham.engageapp.models.Question;
import com.engage.simonnewham.engageapp.models.Survey;
import com.engage.simonnewham.engageapp.models.SurveyResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class that handles activity_survey logic
 * Preforms the downloading and uploading of surveys to and from the database
 */
public class SurveyActivity extends AppCompatActivity {

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    //extra info on user
    String email;
    String surveyID = null;
    String user_group;
    NewsItem newsItem;

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
    TextView uploadText;
    private ProgressBar progressBar;
    private ProgressBar progress;

    //temporary views for begin survey
    TextView title;
    TextView description;
    TextView thank;

    private SurveyDownload surveyDownload;
    private SurveyUpload surveyUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Array of answers
        responses = new ArrayList<>();

        //View elements
        lPanel = findViewById(R.id.survey_panel);
        begin = findViewById(R.id.button_begin);
        submit = findViewById(R.id.button_submit);
        radioGroup = findViewById(R.id.RadioGroup);
        editText = findViewById(R.id.editText);
        questionTitle = findViewById(R.id.text_question);
        next = findViewById(R.id.button_next);
        survey_tick = findViewById(R.id.imageView);
        thank = new TextView(this);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progressBar2);
        uploadText = findViewById(R.id.textUpload);

        //get extra info
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            surveyID = extras.getString("surveyID");
            newsItem = (NewsItem) getIntent().getSerializableExtra("News");
        }

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Survey");
        setSupportActionBar(toolbar);

        if(surveyID.equals("ITEM")){ //if not baseline then load assigned survey
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            surveyDownload = new SurveyDownload(newsItem.getSurvey_id());
            surveyDownload.execute((Void) null);

        }
        //if a basline survey load specific surveyID
        else if (surveyID.equals("BASELINE")){ //if coming from sign up load the baseline
            surveyDownload = new SurveyDownload("5b6d69e9fb9ca80fa8cc14a9");
            surveyDownload.execute((Void) null);
        }
    }

    /**
     * Displays the initial survey title and description after SurveyDownload is finished
     * Uses a survey object created by SurveyDownload
     */
    public void displaySurvey(Survey s){

        if(surveyID.equals("BASELINE")){
            thank.setText("Welcome "+email+"!"+"\n"+"\nThank you for signing up for ENGAGE, please complete the following questionnaire to finish the sign up process! \n");
            thank.setTypeface(null, Typeface.BOLD);
            thank.setTextSize(20);
            lPanel.addView(thank);
        }

        progress.setMax(s.getQuestions().size());

        title = new TextView(this);
        title.setText(s.getName());
        title.setTypeface(null, Typeface.BOLD);
        title.setTextSize(15);
        title.setGravity(Gravity.CENTER);
        lPanel.addView(title);

        description = new TextView(this);
        description.setText("\n"+s.getDescription());
        description.setTextSize(15);
        lPanel.addView(description);
    }

    //Method to clear the title page and display the first question
    public void onBegin(View view){
        if(survey != null){
            thank.setVisibility(View.GONE);
            begin.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            questionTitle.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            onNext();
        }
        else{
            //error
            title = new TextView(this);
            title.setText("Server Error, please try again later.");
            title.setTypeface(null, Typeface.BOLD);
            title.setTextSize(25);
            lPanel.addView(title);
        }
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

        if(current==survey.getQuestions().size()){ //last question has been answered

            next.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            radioGroup.removeAllViews();
            radioGroup.clearCheck();
            radioGroup.setVisibility(View.GONE);
            questionTitle.setVisibility(View.GONE);
            onSubmit();
        }
    }

    /**
     * Method to populate the next question
     * Uses question type to load either editText or radioGroup
     */
    public void onNext(){

        current = (current+1); //for final question it will make current equal to question number
        progress.setProgress(current);

        if( current < survey.getQuestions().size()){

            editText.setText("");
            editText.setVisibility(View.GONE);
            radioGroup.removeAllViews();
            radioGroup.clearCheck();
            radioGroup.setVisibility(View.GONE);

            Question toLoad = survey.getQuestions().get(current);

            int temp = current+1;
            questionTitle.setText("Question "+temp+": "+toLoad.getQuestion());

            String type = toLoad.getType();

            //add option to input response
            if(type.equals("TEXT")){
                editText.setVisibility(View.VISIBLE);
            }

            //add radio buttons for each option
            else if (type.equals("MCQ")){
                radioGroup.setVisibility(View.VISIBLE);
                ArrayList<String> options = toLoad.getOptions();

                for(int i=0; i<options.size();i++) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(options.get(i));
                    radioGroup.addView(radioButton);
                }
            }
        }
    }

    /**
     * onClick listener for when submit is clicked
     * Converts survey to JSON format and pushes document to DB
     */
    public void onSubmit(){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SurveyResponse surveyResponse;

        if (surveyID.equals("BASELINE") || newsItem==null){
            surveyResponse = new SurveyResponse(email, user_group, surveyID, responses,"Baseline" , date );
        }
        else{
            surveyResponse = new SurveyResponse(email, user_group, survey.getName(), responses, newsItem.getName() , date );
        }
        Gson gson = new Gson();
        String json = gson.toJson(surveyResponse);
        progressBar.setVisibility(View.VISIBLE);
        uploadText.setVisibility(View.VISIBLE);
        surveyUpload = new SurveyUpload(email, user_group, json);
        surveyUpload.execute((Void) null);

        Log.i(TAG, "JSON SURVEY RESPONSE"+json);
    }

    /**
     * Onclick listener for when finish button is clicked
     * Directs user back to the home page
     */
    public void onFinish(View view) {
        Intent intent = new Intent(SurveyActivity.this, MainActivity.class);
        //first time load for new users
        if (surveyID.equals("BASELINE")){
            intent.putExtra("load", "online");
        }
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        startActivity(intent);
        finish();
    }

    /**
     * Downloads surveys from the server based on the SurveyID
     */
    public class SurveyDownload extends AsyncTask<Void, Void, String> {

        private final String mSurveyID;

        SurveyDownload(String surveyID) {
            mSurveyID = surveyID;
        }

        /**
         * Connect to API to download survey from the server
         */
        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL("https://engage.cs.uct.ac.za/android/get_survey");

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                OutputStream opStream = httpConn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));

                //Send POST message
                String postData = URLEncoder.encode("survey", "UTF-8")+"="+URLEncoder.encode(mSurveyID, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                opStream.close();

                // Should receive raw JSON of survey
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, ">>>>>Response Result: "+result);
                return result;

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        /**
         * Method to convert JSON survey string into a Survey Object
         */
        protected void onPostExecute(final String result) {
            surveyDownload = null;

            if (result.startsWith("Error") || result.equals("")){
                Log.i(TAG, "Server error:"+result);
            }
            else{
                //convert from JSON string to JSONObject
                ArrayList<Question> questionList = new ArrayList<>(); //hold all questions in survey

                try {
                    JSONObject newJObject = new JSONObject(result); //store survey as JSON object

                    JSONArray questions = newJObject.getJSONArray("questions");
                    if(questions!=null){
                        for (int k=0; k<questions.length(); k++){

                            JSONObject jQuestion = questions.getJSONObject(k);
                            Question question = null;

                            if(jQuestion.getString("type").equals("TEXT")){
                                question = new Question(jQuestion.getString("content"),jQuestion.getString("type"));

                            }

                            else if (jQuestion.getString("type").equals("MCQ")){

                                ArrayList<String> options = new ArrayList<>(); //store an array of answers for MCQ questions

                                JSONArray jAnswers = jQuestion.getJSONArray("answers");

                                if(jAnswers!=null){
                                    for (int i=0; i<jAnswers.length(); i++){

                                        JSONObject jTemp = jAnswers.getJSONObject(i);
                                        String answer = jTemp.getString("content");
                                        options.add(answer);
                                    }
                                }
                                question = new Question(jQuestion.getString("content"),jQuestion.getString("type"), options);
                            }

                            if(question !=null){
                                questionList.add(question);
                            }
                        }
                    }

                    //create Survey object containing question list
                    if(questionList !=null){
                        survey = new Survey(newJObject.getString("_id"), newJObject.getString("name"),
                                newJObject.getString("description"), questionList);
                        progressBar.setVisibility(View.GONE);
                        begin.setClickable(true);
                        displaySurvey(survey);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            surveyDownload = null;
        }
    }

    /**
     * Uploads user survey response to the server via an HTML post request
     */
    public class SurveyUpload extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mGroup;
        private String mResponse;

        SurveyUpload(String email, String user_group, String surveyR) {
            mEmail = email;
            mGroup = user_group;
            mResponse=surveyR;
        }

        /**
         * Connect to API to upload surveyResponse to API
         */
        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL("https://engage.cs.uct.ac.za/android/post_survey"); //will return "Login Success:<user_group>"

                HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
                Log.i(TAG, "Connection established");

                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/json");

                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);

                //place raw JSON into body of post
                OutputStream opStream = httpConn.getOutputStream();
                String str =  mResponse;
                byte[] outputInBytes = str.getBytes("UTF-8");
                opStream.write( outputInBytes );
                opStream.close();

                //Receive result of post message
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpConn.disconnect();

                Log.i(TAG, ">>>>>Response Result: "+result);

                return result;

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        //Method to load success message when survey has been uploaded to server
        protected void onPostExecute(final String result) {
            surveyUpload = null;

            if (result.startsWith("Upload Success")) {
                Log.i(TAG, "SUCCESS");

                progressBar.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                survey_tick.setVisibility(View.VISIBLE);
                uploadText.setVisibility(View.GONE);

                //change prefs so baseline is not loaded on startup
                mPreferences = PreferenceManager.getDefaultSharedPreferences(SurveyActivity.this);
                mEditor = mPreferences.edit();
                mEditor.putString("baseline", "1");
                mEditor.commit();
            }
            else {
                Log.i(TAG, "Server error:"+result);
                onError();
            }
        }

        @Override
        protected void onCancelled() {
            surveyUpload = null;
        }
    }

    //Method for setting up toolbar options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if answering baseline do not give user option to go to home page
        if(!surveyID.equals("BASELINE")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.toolbar_menu, menu);
        }
        return true;
    }

    //Method to handle toolbar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:
                intent = new Intent(SurveyActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                finish();
                return true;
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                return true;
            case R.id.logout:
                intent = new Intent(SurveyActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //clear shared preferences on logout
                mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                mEditor = mPreferences.edit();
                mEditor.clear();
                mEditor.commit();
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Method to display error message to user
    public void onError(){
        TextView error = new TextView(this);
        error.setText("Server Error, please try again later");
        lPanel.addView(error);
    }

    //handle back button logic
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(SurveyActivity.this, ContentActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        intent.putExtra("News", newsItem);
        startActivity(intent);
        finish();
        return true;
    }
}