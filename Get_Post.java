private  class BackgroundTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String str=null;
            try {
                URL url_obj=new URL(params[0]);
                HttpsURLConnection httpsURLConnection =(HttpsURLConnection)url_obj.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.connect();
                InputStream inputStream=null;
                if(httpsURLConnection.getResponseCode() == httpsURLConnection.HTTP_OK){
                    inputStream =httpsURLConnection.getInputStream();
                }

                if(inputStream !=null){
                    str= convertStreamToString(inputStream);
                    try {
                        JSONObject jsonObject= new JSONObject(str);
                        String access_token=jsonObject.getString("access_token");
                        String instance_url=jsonObject.getString("instance_url")+"/services/data/v21.0/sobjects/Lead";

                        JSONObject quoteJson=new JSONObject(quoteData);

                        JSONObject json=new JSONObject();
                        json.put("LastName",quoteJson.getString("quoteLname"));
                        json.put("company",quoteJson.getString("company"));
                        json.put("email",quoteJson.getString("emailId"));
                        json.put("City",quoteJson.getString("city"));
                        json.put("Country",quoteJson.getString("country"));
                        json.put("Description",quoteJson.getString("comments"));
                        json.put("MobilePhone",quoteJson.getString("mobile"));
                        json.put("Phone",quoteJson.getString("phone"));
                        json.put("Website",quoteJson.getString("website"));
                        System.out.println("CRM : "+json);

                        URL insURL=new URL(instance_url);
                        httpsURLConnection =(HttpsURLConnection)insURL.openConnection();
                        httpsURLConnection.setRequestMethod("POST");
                        httpsURLConnection.setRequestProperty("Content-type", "application/json");
                        httpsURLConnection.setRequestProperty("Accept", "application/json");
                        httpsURLConnection.setRequestProperty("Authorization", "OAuth "+access_token);
                        httpsURLConnection.setDoInput(true);
                        httpsURLConnection.setDoOutput(true);

                        OutputStream os = httpsURLConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(json.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        httpsURLConnection.connect();

                        if(httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                            inputStream=httpsURLConnection.getInputStream();
                            str= convertStreamToString(inputStream);
                            System.out.println("CRM: Success");
                        }else{
                            System.out.println("CRM: Failure "+httpsURLConnection.getResponseMessage());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String str) {
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println("CRM: "+str);
            super.onPostExecute(str);
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("IO Exception", e.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("IO Exception", e.toString());
            }
        }
        return sb.toString();
    }
