**YouTube Intelligence RAG**

A Retrieval-Augmented Generation (RAG) application built with Spring AI and Ollama. It allows users to ingest YouTube transcripts and have context-aware conversations about the video content.
Unlike standard LLM chats, this uses a local vector database to "remember" specific video details, providing high-accuracy summaries and Q&A without data leaving your machine.

**Features**
- **Local-First RAG:** Uses Ollama for local LLM (Llama 3.2) and Embeddings (nomic-embed-text).
- **Persistent Vector Storage:** Implements SimpleVectorStore with file-system persistence (vectorstore.json), eliminating the need for Docker or external databases.
- **Spring AI Advisor API:** Utilizes QuestionAnswerAdvisor for a clean, decoupled RAG pipeline.
- **Metadata Filtering:** Prepared for multi-video context management.
- **Production-Ready Patterns:** Includes token-based text splitting and structured AI configuration.

**Tech Stack**
- Framework: **Spring Boot 3.4+**
- AI Orchestration: **Spring AI**
- LLM & Embeddings: **Ollama (Llama 3.2 / Nomic-Embed)**
- Data Persistence: **JSON-based Vector Store**
- Build Tool: **Maven**

**Architecture**
1. **Ingestion:** Raw YouTube transcripts are processed, chunked into 800-token segments with a 150-token overlap, and converted into vector embeddings.
2. **Storage:** Embeddings are stored in a SimpleVectorStore and persisted locally to a JSON file.
3. **Retrieval:** When a user asks a question, the system searches the local JSON store for the most relevant transcript segments.
4. **Generation:** The relevant segments and the user's question are sent to the LLM via the QuestionAnswerAdvisor to generate a grounded response.

**Getting Started**
1. **Prerequisites**
- Java 21 or higher
- Ollama installed and running
- Models pulled: Ollama (Llama 3.2 / Nomic-Embed)
2. **Installation**
- Clone the repository
- Build the project
3. **Running the App**

**API Usage**
1. **Ingest a Video**
- Post the raw transcript for a specific video ID.
- Endpoint: **POST /api/rag/index**
2. **Chat with your Videos**
- Ask questions based on the ingested content.
- Endpoint: **GET /api/rag/ask**
