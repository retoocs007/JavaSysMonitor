# JavaSysMonitor
Small Java SpringBoot REST API service that collects system info.

This is created for personal use with Homepage Custom API widget.

>  https://gethomepage.dev/latest/widgets/services/customapi/


Example how it looks - three large boxes named PROXVM-SRV:
![Proxmox Custom API widgets](https://preview.redd.it/my-homepage-dashbord-with-few-services-and-custom-written-v0-bcqtb2xcxrxc1.jpeg?width=1080&crop=smart&auto=webp&s=573a41d3f9e26c1a5acbb302b5c624378481e580 "Proxmox Custom API widgets")


### Requirements for building:
 - Java 17
 - Gradle 8.7
 - SpringBoot 3.2.5

### Requirements for running:
 - Java 17
 - df package on Linux for getting disk info

### Endpoints
>  ```metrics``` - http://IP_ADDRESS:9314/metrics

>  Optional parameter ```id``` - http://IP_ADDRESS:9314/metrics?id=test

This endpoint returns:
 - cpuLoad
 - usedMemory
 - totalMemory
 - freeMemory
 - disks
   - name
   - totalSpace
   - freeSpace
 - osName
 - osVersion
 - osArch,
 - cpuName
 - mboName
 - upTime

Port can be changed in ```application.properties```.

Optional parameter ```id``` can be used to further separate and identify callers that are calling this endpoint.
It's not required, and it doesn't affect response.

Example response:

```
{
    "cpuLoad": "0,00 %",
    "usedMemory": "7,45 GB",
    "totalMemory": "7,91 GB",
    "freeMemory": "0,46 GB",
    "disks": [
        {
            "name": "C:\\",
            "totalSpace": "222 GB",
            "freeSpace": "28 GB"
        },
        {
            "name": "D:\\",
            "totalSpace": "0 GB",
            "freeSpace": "0 GB"
        }
    ],
    "osName": "Microsoft Windows 10 build 19045",
    "osVersion": "10.0",
    "osArch": "amd64",
    "cpuName": "Intel(R) Core(TM) i3-6100 CPU @ 3.70GHz",
    "mboName": "Dell Inc. - OptiPlex 3040",
    "uptime": "00d:03h:11m:10s"
}
```

In the ```Homepage``` folder there is ```services.yaml``` example how to configure Custom API widget with this service.
Change IP and port accordingly.


### Running on Debian systems
Example how to run on Debian in the background:
> nohup java -jar JavaSysMonitor.war > JavaSysMonitor.log 2>&1 & 

Don't forget to rotate logs or restart service once in a while. Or if you don't need logging you can exclude that part
from the command:

> nohup java -jar JavaSysMonitor.war 2>&1 &

### Other
Potentially this service can be used for getting system information of other PCs (Windows, macOS (not tested), VMs) and display
it in Homepage.