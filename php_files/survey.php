<?php
require_once('db_config.php');

$response = array();
if (isset($_POST['user_id'])) 
  { 
    $user_id = $_POST['user_id'];

    $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die("Error while connecting to db: ".mysql_error());

    $db = mysql_select_db(DB_DATABASE) or die("Error while selecting db: ".mysql_error());
	
    $result = mysql_query("Select SurveyId from SurveySurveyor where SurveyorId='$user_id'", $con);
	
    if (mysql_num_rows($result)>0)
	{
		$response["data"] = array();
		$flag=true;
		while($row=mysql_fetch_array($result))
		{
			$data = array();
			$SurveyId=$row['SurveyId'];
			$data["SurveyId"] = $SurveyId;
			
			$result1 = mysql_query("Select SurveyName, SurveyTypeId from Survey where SurveyId='$SurveyId' and SurveyEndDate>=NOW()", $con);
			if (mysql_num_rows($result1)>0)
			{
					$row1=mysql_fetch_array($result1);
					$SurveyName=$row1['SurveyName'];
					$data["SurveyName"] = $SurveyName;
					$SurveyTypeId=$row1['SurveyTypeId'];
				
					$result2 = mysql_query("Select SurveyTypeName from SurveyType where SurveyTypeId='$SurveyTypeId'", $con);
					if (mysql_num_rows($result2)>0)
					{
						$row2=mysql_fetch_array($result2);
						$SurveyTypeName=$row2['SurveyTypeName'];
						$data["SurveyTypeName"] = $SurveyTypeName;
						
						array_push($response["data"], $data);
					}
					else
					{
						$response["status"] = false;
						$flag=false;
						$response["message"] = "Error in result2.";
						echo json_encode($response);
						return;
					}
			}
			else
			{
				$response["status"] = false;
				$flag=false;
				$response["message"] = "Error in result1.";
				echo json_encode($response);
				return;
			}
	  }
	  
		if($flag==true)
		{
				$response["status"] = true;
				$response["message"] = "Data fetched Successfully.";
				echo json_encode($response);
		}
    	
    } 
	else 
	{
        $response["status"] = false;
        $response["message"] = "Please enter correct user id.";
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