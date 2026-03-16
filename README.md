# IndiaNext_Android
Decentralized agricultural supply chain traceability app built with Jetpack Compose. It utilizes a Jetpack Compose frontend to interact with a Node.js/Express backend and Ethereum blockchain network, providing cryptographically verified "Farm to Fork" transparency.

## 🚀 Features
* **Role-Based Workflows:** Distinct UI dashboards for Farmers, Distributors, and Retailers.
* **Consumer Quick-Scan:** A frictionless guest mode allowing consumers to scan a QR code and instantly view a product's provenance timeline.
* **Cryptographic Verification:** Interfaces with an Express API to mint ERC-1155 batch tokens and track physical custody transfers on the Ethereum network.
* **IPFS Integration:** Handles high-resolution harvest imagery and QA reports stored on decentralized networks.

## 🛠 Tech Stack (Android Client)
* **UI:** Kotlin & Jetpack Compose
* **Networking:** Retrofit & OkHttp
* **Image Loading:** Coil (for IPFS CIDs)
* **Architecture:** MVVM (Model-View-ViewModel)
