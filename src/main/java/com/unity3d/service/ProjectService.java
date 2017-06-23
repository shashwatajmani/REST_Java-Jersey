package com.unity3d.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by shashwatajmani on 5/27/17.
 */
public class ProjectService {

    private String projectFilePath = "/Users/shashwatajmani/Projects.txt";

    /**
     * create() method defines the code to create the Projects.txt file with the input data
     * from the "/createProject" api request body. It accepts the JSON data as a string and validates
     * the data based on various conditions. Finally, it returns the success or failure message which
     * is passed as the response to the api.
     * @param jsonData : JSON input
     * @return : response message (success/failure)
     */
    public String create(String jsonData) {
        String statusMessage = "";
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            jsonData = jsonData.replaceAll("\\n", "");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(jsonData);
            JsonNode idNode = node.get("id");
            JsonNode projectNameNode = node.get("projectName");
            JsonNode creationDateNode = node.get("creationDate");
            JsonNode expiryDateNode = node.get("expiryDate ");
            JsonNode enabledNode = node.get("enabled");
            ArrayNode targetCountriesNode = (ArrayNode) node.get("targetCountries");
            JsonNode costNode = node.get("projectCost");
            ArrayNode targetKeys = (ArrayNode) node.get("targetKeys");
            String projectName = projectNameNode.textValue();
            String creationDate = creationDateNode.textValue();
            String expiryDate = expiryDateNode.textValue();
            String enabled = enabledNode.textValue();

            try{
                Double cost = Double.parseDouble(costNode.asText());
            }catch (Exception e){
                statusMessage = "Cost is not valid";
            }

            if(statusMessage.trim().length() == 0) {
                try {
                    Integer id = Integer.parseInt(idNode.asText());
                } catch (Exception e) {
                    statusMessage = "Id is not valid";
                }
            }

            if(statusMessage.trim().length() == 0) {
                if (projectName.trim().length() == 0) {
                    statusMessage = "Project Name must not be empty";
                } else if (creationDateNode.isNull()) {
                    statusMessage = "Creation date is blank";
                } else if (expiryDateNode.isNull()) {
                    statusMessage = "Expiry date is blank";
                } else if (enabled.trim().length() == 0) {
                    statusMessage = "Kindly enable/disable the project";
                } else if (targetCountriesNode.size() == 0) {
                    statusMessage = "Target Countries are blank";
                } else if(creationDate.trim().length() == 0){
                    statusMessage = "Creation Date is blank";
                } else if(expiryDate.trim().length() == 0){
                    statusMessage = "Expiry Date is blank";
                }

                else {
                    fileWriter = new FileWriter(projectFilePath, true);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(jsonData);
                    bufferedWriter.write("\n");
                    statusMessage = "campaign is successfully created";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusMessage = e.getMessage();

        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                statusMessage = e.getMessage();
            }
        }
        return statusMessage;
    }

    /**
     * retrieve() method defines the code to read the Projects.txt file based on the url parameters
     * from the "/requestProject" api call . It passes the url parameters and compares/checks for the
     * corresponding values in the Projects.txt file. Based on the various conditions, it fetches the
     * appropriate project and returns the "Project Name", "Project Cost" and "Project URL".
     * @param projectId
     * @param country
     * @param number
     * @param keyword
     * @return : Resulting JSON Object
     */
    public String retrieve(Integer projectId, String country, Integer number, String keyword) {

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        double max = 0.0;
        String projectOutput = null;

        try {
            fileReader = new FileReader(projectFilePath);
            bufferedReader = new BufferedReader(fileReader);

            String currentLine;
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();

            while ((currentLine = bufferedReader.readLine()) != null) {

                JsonNode node = objectMapper.readTree(currentLine);

                JsonNode id = node.get("id");

                JsonNode projectName = node.get("projectName");
                JsonNode projectCost = node.get("projectCost");
                JsonNode projectUrl = node.get("projectUrl");
                JsonNode enabled = node.get("enabled");
                JsonNode expiryDateNode = node.get("expiryDate ");

                ArrayNode countries = (ArrayNode) node.get("targetCountries");

                String checkFlag = enabled.textValue();
                String projectUrlCheck = projectUrl.textValue();
                String expiryDate = expiryDateNode.textValue();

                boolean checkID = false;
                if (projectId != null) {
                    if (id.intValue() != projectId) {
                        continue;
                    } else
                        checkID = true;
                } else {
                    if (!checkFlag.equalsIgnoreCase("True") || projectUrlCheck == null
                            || projectUrlCheck.trim().length() == 0) {
                        continue;
                    }

                    try {
                        Date date1 = new SimpleDateFormat("MMddyyyy").parse(expiryDate);
                        Date date = new Date();
                        if (date1.before(date)) {
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (country != null) {
                        if (!this.contains(countries, country)) {
                            continue;
                        }
                    }

                    if (number != null || keyword != null) {
                        ArrayNode targetKeys = (ArrayNode) node.get("targetKeys");
                        Iterator itr = targetKeys.elements();
                        Integer numberValue = 0;
                        String keywordValue = "";

                        boolean checkTarget = false;
                        while (itr.hasNext()) {
                            JsonNode target1 = (JsonNode) itr.next();
                            JsonNode number1 = target1.get("number");
                            JsonNode keyword1 = target1.get("keyword");
                            numberValue = number1.intValue();
                            keywordValue = keyword1.textValue();

                            if (number != null) {
                                if (numberValue >= number) {
                                    checkTarget = true;
                                } else {
                                    checkTarget = false;
                                }
                            }
                            if (keyword != null) {
                                if (keyword.equalsIgnoreCase(keywordValue)) {
                                    checkTarget = true;
                                } else {
                                    checkTarget = false;
                                }
                            }
                            if (checkTarget) {
                                break;
                            }
                        }
                        if (!checkTarget) {
                            continue;
                        }
                    }
                }

                if (max < projectCost.doubleValue()) {
                    max = projectCost.doubleValue();
                    objectNode = updateObjectNode(projectName.textValue(), projectCost.doubleValue(), projectUrl.textValue());
                }

                if (checkID) {
                    break;
                }
            }

            if (objectNode.size() == 0) {
                objectNode.put("message", "no project found");
            }

            projectOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }

        return projectOutput;
    }

    /**
     * method to check if the country value exists in the Target Countries array
     * of the projects from the Projects.txt file
     * @param arrayNode : targetCountries
     * @param value : country parameter
     * @return : boolean
     */
    public boolean contains(ArrayNode arrayNode, String value) {
        Iterator iterator = arrayNode.elements();
        while (iterator.hasNext()) {
            String iterate = ((JsonNode) iterator.next()).textValue();
            if (iterate.equalsIgnoreCase(value))
                return true;
        }
        return false;
    }

    /**
     * method to create the resulting JSON object with the corresponding values
     * @param projectName
     * @param projectCost
     * @param projectUrl
     * @return : JSON Object
     */
    public ObjectNode updateObjectNode(String projectName, double projectCost, String projectUrl) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("projectName", projectName);
        objectNode.put("projectCost", projectCost);
        objectNode.put("projectUrl", projectUrl);
        return objectNode;
    }


    public static void main(String[] args) {
        ProjectService ps = new ProjectService();
//        ps.retrieve(null,null,null, "mobile");

        String jsontry = "{  \n" +
                "    \"id\": 11,  \n" +
                "    \"projectName\": \" project 1\",  \n" +
                "    \"creationDate\": \"05112017 00:00:00\",  \n" +
                "    \"expiryDate \": \"05202017 00:00:00\",  \n" +
                "    \"enabled \": \"True\", \n" +
                "    \"targetCountries\": \"[USA, India]\", \n" +
                "    \"projectCost\": 5.5,  \n" +
                "    \"projectUrl\": \"http://www.unity3d.com\",  \n" +
                "    \"targetKeys\": [{  \n" +
                "            \"number\": 25,  \n" +
                "            \"keyword\": \"movie\"  \n" +
                "        },  \n" +
                "        {  \n" +
                "            \"number\": 30,  \n" +
                "            \"keyword\": \"sports\"  \n" +
                "        }]  \n" +
                "} \n";

        ps.create(jsontry);

    }
}

