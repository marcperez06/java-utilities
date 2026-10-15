# java-utilities

[![Maven Central](https://img.shields.io/badge/maven-v0.1.9-blue.svg)](http://maven.apache.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A comprehensive collection of useful Java utility classes and helper methods designed to simplify common programming tasks across various projects (file handling, string manipulation, database access, REST clients, cryptography, excel/csv parsing, and more).

## Features & Modules

- **File Utilities (`FileUtils`)**: Methods for checking, creating, reading (simple & multi-file, CSV, byte arrays), writing, and deleting files and directories.
- **String Utilities (`StringUtils`, `RegexUtils`)**: Text manipulation, capitalization, formatting, and regular expression helpers.
- **Collection Utilities (`ArrayUtils`, `ListUtils`, `MapUtils`)**: Helpers for handling arrays, lists, and maps efficiently.
- **Date Utilities (`DateUtils`)**: Date formatting, parsing, and manipulation helpers.
- **Database Utilities (`Database`, `BaseDAO`, `DatabaseUtils`)**: Database connectivity and DAO support for MySQL, Oracle, and SQL databases.
- **Excel & CSV (`Excel`, `ExcelUtils`, `Csv`)**: Reading and writing Excel spreadsheets and CSV files.
- **API & REST Clients (`BaseRestClient`, `UnirestClient`, `Request`)**: HTTP requests and REST client wrappers.
- **Cryptography (`AESCrypto`, `CryptoUtils`)**: Encryption and decryption utilities.
- **JSON & XML (`GsonUtils`, `Xml`, `XmlUtils`)**: JSON serialization/deserialization and XML parsing/manipulation.
- **Other Utilities**: Threading, reflection, timers, validation, system info, and test data generators.

## Project Structure

```text
java-utilities/
├── pom.xml
├── README.md
├── LICENSE
└── src/
    ├── main/
    │   └── java/
    │       └── io/github/marcperez06/java_utilities/
    │           ├── api/             # REST clients and HTTP request utilities
    │           ├── batch/           # Batch execution utilities
    │           ├── collection/      # Array, List, and Map helper utilities
    │           ├── crypto/          # AES and general cryptography helpers
    │           ├── csv/             # CSV processing utilities
    │           ├── database/        # Database connectivity, DAO base classes & MySQL/Oracle support
    │           ├── date/            # Date and time manipulation utilities
    │           ├── excel/           # Excel spreadsheet parsing utilities
    │           ├── factory/         # Generic factory implementations
    │           ├── file/            # File and directory management (FileUtils)
    │           ├── gherkin/         # Gherkin syntax parser and objects
    │           ├── json/            # Gson serialization/deserialization utilities
    │           ├── logger/          # Logging wrapper utilities
    │           ├── numbers/         # Number formatting and validation utilities
    │           ├── pdf/             # PDF parsing and validation utilities
    │           ├── properties/      # Environment and configuration properties manager
    │           ├── reflection/      # Reflection helper utilities
    │           ├── robot/           # Robot framework/automation utilities
    │           ├── servlets/        # Servlet and socket servlet helpers
    │           ├── strings/         # String and regular expression manipulation utilities
    │           ├── system/          # System utilities
    │           ├── testdata/        # Test data collection and random data generators
    │           ├── threads/         # Multi-threading utilities and tasks
    │           ├── timer/           # Execution timer utility
    │           ├── uri/             # URI and endpoint utilities
    │           ├── validation/      # Validation helpers
    │           └── xml/             # XML parsing and utility classes
    └── test/
        └── java/                    # Unit tests and test examples
```

## Requirements

- **Java JDK**: 1.8 or higher
- **Build Tool**: Maven 3.x

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.marcperez06</groupId>
    <artifactId>java-utilities</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Author

**Marc Pérez Rodríguez**
- GitHub: [marcperez06](https://github.com/marcperez06)

## License

This project is licensed under the [MIT License](LICENSE).