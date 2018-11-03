[![HitCount](http://hits.dwyl.io/wihoho/wihoho/Android-XMPP.svg)](http://hits.dwyl.io/wihoho/wihoho/Android-XMPP)
# Abstract
Cloud computing could be deployed in the process of designing a mobile application in order to overcome the limits of smartphones like low-speed CPU, small memory and low battery. This project basically constructs an android application with the cloud assisted which allows people to watch the video and chat with others simultaneously. This application uses video from Youtube as the content provider. Furthermore, it chooses the App Engine powered by Google as the cloud. The chatting service is built on Extensible Messaging and Presence Protocol (XMPP).

# Design
This application consists of two components: chatting service and video streaming. The backbone that enables chatting service is Extensible Messaging and Presence Protocol (XMPP). XMPP is an open-standard communications protocol for message-oriented middleware based on XML, and it is designed for near-real-time, extensible instant messaging (IM), presence information, and contact list maintenance [2]. Furthermore, this project needs to support group chatting since the number of people who are watching the same video is unsure. That’s the reason why App Engine plays an important role in this project. Google App Engine is a platform as a service (PaaS) cloud computing platform for developing and hosting web applications in Google-managed data centers. Applications are sandboxed and run across multiple servers [3]. 

The basic architecture of this project is as Figure 1 below. App Engine plays as a server and stores relevant information in Datastore. Meanwhile, it also serves the android client when requested. The way of sending data from servers to their applications on Android device is through Android Cloud to Device Messaging (C2DM), which provides a simple lightweight mechanism that servicers can use to tell mobile applications to contact the server directly, to fetch updated application or user data.

![Figure 1](https://lh6.googleusercontent.com/-vRTfLBoQUcs/UTBGNBwN6QI/AAAAAAAAA2U/xTpmw8V_RcU/s464/Architecture.png)

# Program
- Chatting Service WorkFlow

![](https://lh3.googleusercontent.com/-RG0Lw2AFidE/UTBGNImCHbI/AAAAAAAAA2M/GEiElFLieRs/s572/Chatting%2520Activity.png)

- Group Chatting Service WorkFlow

![](https://lh6.googleusercontent.com/-LDZCVHgIkjM/UTBGNBxF6kI/AAAAAAAAA2Q/A04WkFBCokY/s602/Group%2520Chatting%2520Service.png)

# Conclusion and Future Work
The aim of this project was to develop a mobile application with the cloud assisted to help the customers watch videos and chat with the others at the same time. This application was successfully developed and tested.

The video streaming service is achieved by following the Youtube API and Android API. On the other hand, the chatting service is based on XMPP. Furthermore, the App Engine helps to forward messages to achieve the so-called group chatting.

Future improvements could include making the cloud do more things for the mobile application. For example, the process of calculating the correct url for each video could be deployed on the cloud. The advantages are obvious. However, it also increases the complexity.
