package main

import (
	"bytes"
	"fmt"
	"io"
	"mime"
	"net/http"
	"os"
)

func main() {
	fileContent, err := os.ReadFile("animation-script-demo.txt")
	if err != nil {
		panic(err)
	}

	postBody := bytes.NewBuffer(fileContent)

	resp, err := http.Post("http://127.0.0.1:8080/script", "plain/text", postBody)
	if err != nil {
		panic(err)
	}

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		fmt.Printf("string(body): %v\n", string(body))
	}

	fileName, err := parseContentDisposition(resp.Header)
	if err != nil {
		panic(err)
	}

	file, err := os.Create(fileName)
	if err != nil {
		panic(err)
	}

	_, err = io.Copy(file, resp.Body)
	if err != nil {
		panic(err)
	}

	fmt.Println("Video file downloaded successfully as 'downloaded_video.mp4'")
}

func parseContentDisposition(header http.Header) (string, error) {
	// Get the Content-Disposition header from the request
	contentDisposition := header.Get("Content-Disposition")
	if contentDisposition == "" {
		return "", fmt.Errorf("Content-Disposition header not found")
	}

	// Parse the Content-Disposition header
	_, params, err := mime.ParseMediaType(contentDisposition)
	if err != nil {
		return "", fmt.Errorf("error parsing Content-Disposition header: %v", err)
	}

	// Extract the filename parameter, if present
	filename := params["filename"]
	if filename == "" {
		return "", fmt.Errorf("filename parameter not found in Content-Disposition")
	}

	return filename, nil
}
