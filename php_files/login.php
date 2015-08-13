<?php
require_once('db_config.php');

$response = array();
if (isset($_POST['user_name']) && isset($_POST['password']) ) 
  { 
    $user_name = $_POST['user_name'];
    $password = $_POST['password'];

    $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die("Error while connecting to db: ".mysql_error());

    $db = mysql_select_db(DB_DATABASE) or die("Error while selecting db: ".mysql_error());
	
    $result = mysql_query("Select SurveyorId from Surveyor where Login='$user_name' and Password='$password'", $con);
	
    if (mysql_num_rows($result)>0)
	{
		$row=mysql_fetch_array($result);
		$id=$row['SurveyorId'];
		$response["SurveyorId"] = $id;
        $response["status"] = true;
        $response["message"] = "Logged in successfully.";
        echo json_encode($response);
    } 
	else 
	{
        $response["status"] = false;
        $response["message"] = "Please enter correct user name and password.";
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