package com.dev.simplespa.module.file;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.dev.simplespa.model.domain.DownloadedFileDto;
import com.dev.simplespa.model.domain.FileInfoDto;
import com.dev.simplespa.model.domain.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AzureBlobService {

	private final BlobContainerClient containerClient;

	public AzureBlobService(
		@Value("${azure.storage.connection-string}") String connectionString,
		@Value("${azure.storage.container-name}") String containerName
	) {
		BlobServiceClient blobServiceClient =
			new BlobServiceClientBuilder()
				.connectionString(connectionString)
				.buildClient();

		this.containerClient = blobServiceClient
			.getBlobContainerClient(containerName);
	}

	public UploadedFile uploadFile(MultipartFile file) throws IOException {
		String blobName = UUID.randomUUID() + "-" + file.getOriginalFilename();

		BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
		BlobClient blobClient = containerClient.getBlobClient(blobName);

		blobClient.upload(file.getInputStream(), file.getSize(), true);
		blobClient.setHttpHeaders(headers);

		return UploadedFile.builder()
			.name(blobName)
			.originalName(file.getOriginalFilename())
			.uploadedAt(LocalDateTime.now())
			.build();
	}

	public DownloadedFileDto downloadFile(String blobName) {
		BlobClient blobClient = containerClient.getBlobClient(blobName);

		byte[] content = blobClient.downloadContent().toBytes();
		return DownloadedFileDto.builder()
			.content(content)
			.contentType(blobClient.getProperties().getContentType())
			.fileName(blobName)
			.build();
	}

	public List<FileInfoDto> listFiles() {

		List<FileInfoDto> files = new ArrayList<>();

		for (BlobItem blobItem : containerClient.listBlobs()) {

			BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
			BlobProperties properties = blobClient.getProperties();

			files.add(new FileInfoDto(
				blobItem.getName(),
				properties.getContentType(),
				properties.getBlobSize(),
				blobClient.getBlobUrl(),
				properties.getCreationTime().toInstant())
			);
		}

		return files;
	}

	public void deleteFile(String blobName) {
		BlobClient blobClient = containerClient.getBlobClient(blobName);
		blobClient.delete();
	}
}
