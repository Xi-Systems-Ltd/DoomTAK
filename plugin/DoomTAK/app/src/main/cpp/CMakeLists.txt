cmake_minimum_required(VERSION 3.4.1)

# Set the project name
project(doomtak)

# Set C++ standard and optimization flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -O3 -D__GXX_EXPERIMENTAL_CXX0X__")

file(GLOB DOOM_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/DOOM/*.c")

# Add the source files
add_library(doomtak SHARED doomtak.cpp ${DOOM_SOURCES})

find_library(log-lib log)
find_library(android-lib android)
find_library(gles-lib GLESv2)

# Include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/DOOM)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../../../build/generated/jni)

# Link libraries
target_link_libraries(
        doomtak
        log
        ${log-lib}
        ${android-lib}
        ${gles-lib}
)
