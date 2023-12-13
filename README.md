# weather
A simple weather app for Western Australia

# Introduction

This is a simple web app to add to my portfolio, focused on Western Australia and using the public data from the Bureau Of Meteorology. They provide their data as xml files accessible from ftp, broadly this project will likely include a simple web router, a UI to select a location and show the forecast from the data set, and lastly a system to routinely update our known data.

# Data Update function 

I'm currently thinking there might be a cronjob equivalent in docker that can trigger an update daily? Or trigger it from the web access, there are trade-offs for each.

I spent a little time thinking about when to trigger the update function, and have decided to check/invalidate a server-side cache when the website is accessed.

[Update Trigger Assessment](dev/docs/Update Trigger Assessment.ods)

## Data source

The BOM has an ftp server; ftp://ftp.bom.gov.au/anon/gen/fwo/

There are many files with different sets of data, I've selected the Short Forecast for the whole state IDW14199.xml.

Final url  ftp://ftp.bom.gov.au/anon/gen/fwo/IDW14199.xml

### Data Shape

I'll need to explore the data packet for a moment, extract any information I'll want to use, and figure the shape of the data to load into Prisma. 

All data is within a greater `<product>` tag, the metadata is stored in an `<amoc>` tag.

| Data       | Path                  | Example              | Note                     |
|------------|-----------------------|----------------------|--------------------------|
| Expiration | `<amoc><expiry-time>` | 2023-12-13T01:22:44Z | To test cached validity. |

The bulk of the data is a `<forcecast>`, which is a set of `<area>`s.

An `<area>` can have one of three types;

- region 
- public-district 
- location

Public Districts have a Parent ID referring to the region they belong to.

Locations have a Parent ID referring to the Public District they belong to, and a set of Forecast Periods.

#### Forecast Period 

``` xml
<forecast-period index="0" start-time-local="2023-12-12T05:00:00+08:00" end-time-local="2023-12-13T00:00:00+08:00" start-time-utc="2023-12-11T21:00:00Z" end-time-utc="2023-12-12T16:00:00Z">
    <element type="forecast_icon_code">1</element>
    <element type="air_temperature_maximum" units="Celsius">37</element>
    <text type="precis">Sunny.</text>
    <text type="probability_of_precipitation">0%</text>
</forecast-period>
```

### Notes

I learned that the minimum temperature is optional (not present on index 0 periods), and there is an optional range for the amount of rain.

### Data importer

I'm going to make this in clojure. I like it a lot for etl.

# Web router 

The selection of create-t3-app should make my router un-needed.

# UI 

I'd like to do this one in full TypeScript, have a build system package it up into a javascript to have the web router server the file.

First things first is to pick a full-stack framework. I've chosen create-t3-app, mostly due to the preference for TypeScript and Prisma.

[TS Build System Assessment](dev/docs/TS Build System Assessment.ods)
 
