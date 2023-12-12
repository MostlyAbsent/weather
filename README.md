# weather
A simple weather app for Western Australia

# Introduction

This is a simple web app to add to my portfolio, focused on Western Australia and using the public data from the Bureau Of Meteorology. They provide their data as xml files accessible from ftp, broadly this project will likely include a simple web router, a UI to select a location and show the forecast from the data set, and lastly a system to routinely update our known data.

# Data Update function 

I'm currently thinking there might be a cronjob equivalent in docker that can trigger an update daily? Or trigger it from the web access, there are trade-offs for each.

I spent a little time thinking about when to trigger the update function, and have decided to check/invalidate a server-side cache when the website is accessed.

[Update Trigger Assessment](dev/docs/Update Trigger Assessment.ods)

