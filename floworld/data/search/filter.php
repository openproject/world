<?php
$db = sqlite_open("floworld_data.db");
$sql = "create table test (id INTEGER PRIMARY KEY,movie text,url text,content text ,time   datatime);";
$result = sqlite_query($db, $sql);
if ($result)
{
echo "init database successful!";
}
else
{
echo "init!";
}
phpinfo();
?>