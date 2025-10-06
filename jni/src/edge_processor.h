#pragma once
#include <cstdint>
#include <vector>

enum class EdgeMode : int32_t { Grayscale = 0, Canny = 1 };

struct ImageView {
	const uint8_t* data;
	int32_t width;
	int32_t height;
	int32_t stride;
	int32_t channels; // 1=GRAY, 3=RGB, 4=RGBA
};

struct ImageBuffer {
	std::vector<uint8_t> bytes;
	int32_t width = 0;
	int32_t height = 0;
	int32_t stride = 0;
	int32_t channels = 0;
};

// Processes an RGBA input and writes GRAY or RGBA output depending on mode.
ImageBuffer processFrameRGBA(const ImageView& input, EdgeMode mode);
