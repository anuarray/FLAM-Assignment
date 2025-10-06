#include "edge_processor.h"
#include <algorithm>
#include <cmath>

#ifdef OpenCV_FOUND
#include <opencv2/imgproc.hpp>
#include <opencv2/core.hpp>
#endif

static inline uint8_t clampToByte(int v) {
	return static_cast<uint8_t>(std::min(255, std::max(0, v)));
}

static ImageBuffer fallbackGray(const ImageView& input) {
	ImageBuffer out;
	out.width = input.width;
	out.height = input.height;
	out.channels = 1;
	out.stride = input.width * out.channels;
	out.bytes.resize(out.stride * out.height);
	for (int y = 0; y < input.height; ++y) {
		for (int x = 0; x < input.width; ++x) {
			const uint8_t* p = input.data + y * input.stride + x * input.channels;
			int r = p[0];
			int g = p[1];
			int b = p[2];
			int gray = (r * 299 + g * 587 + b * 114 + 500) / 1000; // BT.601
			out.bytes[y * out.stride + x] = clampToByte(gray);
		}
	}
	return out;
}

// Very naive edge detection using Sobel-like kernels on grayscale fallback
static ImageBuffer fallbackCannyLike(const ImageView& input) {
	ImageBuffer gray = fallbackGray(input);
	ImageBuffer out;
	out.width = gray.width;
	out.height = gray.height;
	out.channels = 1;
	out.stride = gray.width;
	out.bytes.assign(out.stride * out.height, 0);
	for (int y = 1; y < gray.height - 1; ++y) {
		for (int x = 1; x < gray.width - 1; ++x) {
			int gx = -gray.bytes[(y-1)*gray.stride + (x-1)] - 2*gray.bytes[y*gray.stride + (x-1)] - gray.bytes[(y+1)*gray.stride + (x-1)]
					+ gray.bytes[(y-1)*gray.stride + (x+1)] + 2*gray.bytes[y*gray.stride + (x+1)] + gray.bytes[(y+1)*gray.stride + (x+1)];
			int gy = -gray.bytes[(y-1)*gray.stride + (x-1)] - 2*gray.bytes[(y-1)*gray.stride + x] - gray.bytes[(y-1)*gray.stride + (x+1)]
					+ gray.bytes[(y+1)*gray.stride + (x-1)] + 2*gray.bytes[(y+1)*gray.stride + x] + gray.bytes[(y+1)*gray.stride + (x+1)];
			int mag = static_cast<int>(std::sqrt(static_cast<float>(gx*gx + gy*gy)));
			out.bytes[y*out.stride + x] = clampToByte(mag);
		}
	}
	return out;
}

ImageBuffer processFrameRGBA(const ImageView& input, EdgeMode mode) {
#ifdef OpenCV_FOUND
	// OpenCV path
	cv::Mat rgba(input.height, input.width, CV_8UC4, const_cast<uint8_t*>(input.data), input.stride);
	cv::Mat gray;
	cv::cvtColor(rgba, gray, cv::COLOR_RGBA2GRAY);
	if (mode == EdgeMode::Grayscale) {
		ImageBuffer out;
		out.width = gray.cols;
		out.height = gray.rows;
		out.channels = 1;
		out.stride = gray.cols;
		out.bytes.assign(gray.data, gray.data + gray.total());
		return out;
	}
	cv::Mat edges;
	cv::Canny(gray, edges, 100, 200);
	ImageBuffer out;
	out.width = edges.cols;
	out.height = edges.rows;
	out.channels = 1;
	out.stride = edges.cols;
	out.bytes.assign(edges.data, edges.data + edges.total());
	return out;
#else
	// Fallback path
	if (mode == EdgeMode::Grayscale) return fallbackGray(input);
	return fallbackCannyLike(input);
#endif
}
