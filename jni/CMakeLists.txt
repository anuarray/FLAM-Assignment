cmake_minimum_required(VERSION 3.22.1)
project(edgeproc)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(edgeproc SHARED
	src/native-lib.cpp
	src/edge_processor.cpp
)

target_include_directories(edgeproc PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/src)

# Try to find OpenCV. Set OpenCV_DIR to your Android OpenCV SDK cmake folder if needed.
find_package(OpenCV QUIET)
if(OpenCV_FOUND)
	message(STATUS "OpenCV found: ${OpenCV_VERSION}")
	target_include_directories(edgeproc PRIVATE ${OpenCV_INCLUDE_DIRS})
	target_link_libraries(edgeproc ${OpenCV_LIBS})
else()
	message(WARNING "OpenCV not found. Building with fallback CPU grayscale/canny.")
endif()

find_library(log-lib log)

target_link_libraries(edgeproc
	${log-lib}
)
