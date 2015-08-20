<?php
$response = array();
if (isset($_POST['SurveyId'])) 
  { 
    $SurveyId = $_POST['SurveyId'];
    
	require_once('db_config.php');

    $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die("Error while connecting to db: ".mysql_error());

    $db = mysql_select_db(DB_DATABASE) or die("Error while selecting db: ".mysql_error());
	
    $result = mysql_query("Select SurveyQuestionId, QuestionID from SurveyQuestion where SurveyId='$SurveyId'", $con);
	
    if (mysql_num_rows($result)>0)
	{
		$response["data"] = array();
		
		while ($row = mysql_fetch_array($result)) 
		{
			// temp user array
			$questions = array();
			
			$SurveyQuestionId = $row['SurveyQuestionId'];
			$QuestionID = $row['QuestionID'];
			
			$questions["SurveyQuestionId"] = $SurveyQuestionId;
			
			$result2 = mysql_query("Select Question from Question where QuestionID='$QuestionID'", $con);
			
			$result3 = mysql_query("Select QuestionOptionId, QuestionOption, QuestionOptionTypeId from QuestionOption where QuestionID='$QuestionID'", $con);
			
			if(mysql_num_rows($result2)>0 && mysql_num_rows($result3)>0)
			{
				$row2 = mysql_fetch_array($result2);
				$Question = $row2['Question'];
				$questions["Question"] = $Question;
				
				$QuestionOptionId="";
				$QuestionOption="";
				$QuestionOptionTypeId="";
				$flag=false;
				
				while($row3=mysql_fetch_array($result3)){
					$questions["options"]=array();					
					$options=array();
					
					if($flag==true){
						$QuestionOptionId = $QuestionOptionId.",".$row3['QuestionOptionId'];
						$QuestionOption = $QuestionOption.",".$row3['QuestionOption'];
						$QuestionOptionTypeId = $row3['QuestionOptionTypeId'];
					}else{
						$QuestionOptionId = $row3['QuestionOptionId'];
						$QuestionOption = $row3['QuestionOption'];
						$QuestionOptionTypeId = $row3['QuestionOptionTypeId'];
						$flag=true;
					}

					$options["QuestionOptionId"] = $QuestionOptionId;
					$options["QuestionOption"] = $QuestionOption;
					$options["QuestionOptionTypeId"] = $QuestionOptionTypeId;

					array_push($questions["options"], $options);
				}
				
			}
			
			// push single product into final response array
			array_push($response["data"], $questions);
		}
		
        $response["status"] = true;
        $response["message"] = "Question list is available.";
        echo json_encode($response);
    } 
	else 
	{
        $response["status"] = false;
        $response["message"] = "Question list is not available.";
        echo json_encode($response);
    }
 
 }
 else 
 {
    $response["status"] = false;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
} 
?>