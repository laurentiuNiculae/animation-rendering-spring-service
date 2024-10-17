package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime"
	"net/http"
	"os"
	"time"
)

func main() {
	videoURL, err := submitScript()
	if err != nil {
		panic(err)
	}

	req, err := http.NewRequest(http.MethodGet, videoURL, http.NoBody)
	if err != nil {
		panic(err)
	}

	err = retryBackoff(1*time.Second, func() (bool, error) {
		resp, err := http.DefaultClient.Do(req)
		if err != nil {
			return false, err
		}

		fileName, err := parseContentDisposition(resp.Header)
		if err != nil {
			body, _ := io.ReadAll(resp.Body)
			resp := GetFileResponse{}

			err = json.Unmarshal(body, &resp)
			if err != nil {
				fmt.Printf("WTF err: %v\n", err)
				return false, err
			}

			switch resp.VideoRenderStaus {
			case "Started":
				fmt.Println("Let's wait a bit more")
				return true, nil
			case "Unknown":
				return false, fmt.Errorf("some weird error happened: %s", err)
			case "Successful":
				return false, nil
			case "Unsuccessful":
				return false, fmt.Errorf("some normal error happened : %s", resp.Error)
			default:
				return false, err
			}
		}

		file, err := os.Create(fileName)
		if err != nil {
			return false, err
		}

		_, err = io.Copy(file, resp.Body)
		if err != nil {
			return false, err
		}

		return false, nil
	})()
	if err != nil {
		panic("After retry: " + err.Error())
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

func retryBackoff(initialWait time.Duration, task func() (bool, error)) func() error {
	return func() error {
		maxRetries := 15

		for range maxRetries {
			time.Sleep(initialWait)

			continueRetry, err := task()
			if err != nil {
				return err
			}

			if !continueRetry {
				return nil
			}

			initialWait = time.Duration(float64(initialWait) * 1.4)
		}

		return fmt.Errorf("timeout, exeeded retries")
	}
}

func submitScript() (string, error) {
	fileContent, err := os.ReadFile("animation-script-demo.txt")
	if err != nil {
		panic(err)
	}

	postBody := bytes.NewBuffer(fileContent)

	req, err := http.NewRequest(http.MethodPost, "http://127.0.0.1:8080/script", postBody)
	if err != nil {
		panic(err)
	}

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		panic(err)
	}

	body, _ := io.ReadAll(resp.Body)

	fileResp := SubmitScriptResponse{}
	err = json.Unmarshal(body, &fileResp)
	if err != nil {
		fmt.Printf("WTF err: %v\n", err)
	}

	switch fileResp.VideoRenderStaus {
	case "Started":
		fmt.Println("Started Successfuly")
		return fileResp.VideoURL, nil
	case "Unknown":
		return "", fmt.Errorf("some weird error happened: %w", err)
	case "Successful":
		fmt.Println("It's already done!")
		return fileResp.VideoURL, nil
	case "Unsuccessful":
		return "", fmt.Errorf("some normal error happened : %w", err)
	default:
		return "", err
	}
}

type SubmitScriptResponse struct {
	VideoRenderStaus string `json:"videoRenderStaus"`
	VideoURL         string `json:"resourceUrl"`
}

type GetFileResponse struct {
	VideoRenderStaus string `json:"videoRenderStaus"`
	Error            string `json:"error"`
}
