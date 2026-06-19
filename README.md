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

### Endpoints
>  GET ```metrics``` - http://IP_ADDRESS:9314/metrics

Optional parameter ```id``` - http://IP_ADDRESS:9314/metrics?id=test

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
 - osArch
 - cpuName
 - mboName
 - upTime

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
Homepage example:
```
    - "PROXVM-SRV":
        description: CPU and RAM
        icon: /icons/proxmox.svg
        siteMonitor: https://IP_ADDRESS:8006/
        widget:
          type: customapi
          url: http://IP_ADDRESS:9314/metrics?id=CPUandRAM
          refreshInterval: 10000
          display: list
          mappings:
            - field: cpuLoad
              label: CPU load
              format: text
            - field: totalMemory
              label: Total memory
              format: text
            - field: usedMemory
              label: Used memory
              format: text
            - field: freeMemory
              label: Free memory
              format: text
            - field: uptime
              label: Uptime
              format: text
```

>  GET ```metrics-as-list``` - http://IP_ADDRESS:9314/metrics-as-list

Optional parameter ```id``` - http://IP_ADDRESS:9314/metrics-as-list?id=test

Optional parameter ```allowedTypes``` - http://IP_ADDRESS:9314/metrics-as-list?allowedTypes=OS,KERNEL,ARCH,CPU_NAME,MBO

Combined example - - http://IP_ADDRESS:9314/metrics-as-list?id=test&allowedTypes=OS,KERNEL,ARCH,CPU_NAME,MBO

| allowedTypes avaliable values |
|-------------------------------|
| CPU_LOAD                      |
| MEM_TOTAL                     |
| MEM_USED                      |
| MEM_FREE                      |
| OS                            |
| KERNEL                        |
| ARCH                          |
| CPU_NAME                      |
| MBO                           |
| UPTIME                        |
| DISK                          |


This endpoint returns based on applied filter:
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
- osArch
- cpuName
- mboName
- upTime

Optional parameter ```id``` can be used to further separate and identify callers that are calling this endpoint.
It's not required, and it doesn't affect response.

Optional parameter ```allowedTypes``` can be sent with list of available values (see table above).
If allowedTypes is not sent then all metrics will be returned.
List is also order, how values are sent that way will be ordered.
Labels are fixed at the moment. Future plan is to add POST method and in the body user can sent list of values for replacing labels.

This endpoint is intended for use with ```Homepage customapi dynamic-list``` feature.

Example response:

```
[
  {
    "label": "CPU load",
    "value": "43.54 %",
    "type": "CPU_LOAD"
  },
  {
    "label": "Total memory",
    "value": "15.94 GB",
    "type": "MEM_TOTAL"
  },
  {
    "label": "Used memory",
    "value": "11.44 GB",
    "type": "MEM_USED"
  },
  {
    "label": "Free memory",
    "value": "4.51 GB",
    "type": "MEM_FREE"
  },
  {
    "label": "OS",
    "value": "Microsoft Windows 11 build 26100",
    "type": "OS"
  },
  {
    "label": "Kernel",
    "value": "10.0",
    "type": "KERNEL"
  },
  {
    "label": "Arch",
    "value": "amd64",
    "type": "ARCH"
  },
  {
    "label": "CPU",
    "value": "Intel(R) Core(TM) i5-7600 CPU @ 3.50GHz",
    "type": "CPU_NAME"
  },
  {
    "label": "MBO",
    "value": "ASUSTeK COMPUTER INC. - unknown",
    "type": "MBO"
  },
  {
    "label": "Uptime",
    "value": "00d:01h:42m:49s",
    "type": "UPTIME"
  },
  {
    "label": "C:\\",
    "value": "Total: 222 GB  Free: 95 GB",
    "type": "DISK"
  },
  {
    "label": "D:\\",
    "value": "Total: 223 GB  Free: 85 GB",
    "type": "DISK"
  }
]
```
Homepage example:
```
    - "PROXVM-SRV":
        description: CPU and RAM
        icon: /icons/proxmox.svg
        siteMonitor: https://IP_ADDRESS:8006/
        widget:
          type: customapi
          url: http://IP_ADDRESS:9314/metrics-as-list?id=CPUandRAM&allowedTypes=CPU_LOAD,MEM_TOTAL,MEM_USED,MEM_FREE,UPTIME
          refreshInterval: 10000
          display: dynamic-list
          mappings:
            items:
            name: label
            label: value
            limit: 5
```

### Homepage examples
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

Port can be changed in ```application.properties```.