APP Fall 2024

Saran Kirthic Sivakumar - 40274070 - Word Stats 
Duriapandiyan Anbumani Poongathai - 40273111 - Channel Profile
Sushanth Ravishankar - 40267400 - Tags

# TubeLytics 📊🎥

Reactive TubeLytics is a Play Framework-based web application designed to analyze and visualize data from the YouTube API. This project provides various insights, including channel profile information, word statistics, video tags, and sentiment analysis of video descriptions. The second part of the project extends the functionality using **WebSockets** and **Akka Actors** to create a reactive, real-time server-push application.

---

## 🛠 Tech Stack

- **Java 8+**
- **Play Framework**
- **Akka Actors**
- **WebSockets**
- **YouTube Data API v3**
- **Google Guice** (Dependency Injection)
- **JUnit / Mockito** (Unit Testing)
- **SBT** (Build Tool)
- **Bootstrap / CSS** (Basic Styling)

---

## 📚 Features

### ✅ Phase 1: Basic TubeLytics (Play Framework)

- 🔍 **YouTube Search Integration**  
  Users can input a search query to retrieve video results from YouTube.

- 📈 **Channel Profile Page**  
  Displays information such as channel title, description, creation date, subscriber count, and total views.

- 🧠 **Word Statistics**  
  Parses and counts word occurrences from video descriptions in search results.

- ❤️ **Sentiment Analysis**  
  Performs basic sentiment analysis (positive/negative/neutral) on video descriptions.

### ⚡ Phase 2: Reactive TubeLytics (Akka + WebSockets)

- 🟢 **Akka Actor System**  
  Introduces custom Akka Actors for handling requests like fetching channel profiles, generating word stats, and analyzing video tags asynchronously.

- 🔄 **WebSocket Integration**  
  Live updates are pushed to the client via WebSockets (reactive server-push model).

- 💬 **Real-time Feedback**  
  Users receive real-time progress and results (channel info, word stats, sentiment analysis) as Akka actors process them.

---

