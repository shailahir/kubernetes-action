package com.shailahir.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KubeAuth {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubeAuth.class.getName());

    private static final String KUBE_CONFIG_FILENAME = "kubeconfig";
    private static final String KUBE_CONFIG_EXTENSION = ".yml";

    public static final String KUBECONFIG_FILE = KUBE_CONFIG_FILENAME + KUBE_CONFIG_EXTENSION;

    private KubeAuthType kubeAuthType;

    private void setupKubeConfig(String kubeConfigFile) {
        LOGGER.debug("Setup Kube Config File: {}", kubeConfigFile);

        try {
            Path kubeconfigPath = Files.createTempFile(KUBE_CONFIG_FILENAME, KUBE_CONFIG_EXTENSION);

            LOGGER.debug("Writing Kubeconfig file: {}", kubeconfigPath.toString());
            Files.writeString(kubeconfigPath, kubeConfigFile);

            LOGGER.debug("Kubeconfig file written to: {}", kubeconfigPath.toString());
        } catch (IOException e) {
            LOGGER.error("Error while writing Kubeconfig file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public KubeAuthType getKubeAuthType() {
        return this.kubeAuthType;
    }

    public void setup() {
        LOGGER.debug("Setup Kube Auth Type");
        String kubeconfig = System.getenv("INPUT_KUBECONFIG");

        if (kubeconfig != null && !kubeconfig.trim().isEmpty()) {
            LOGGER.debug("Kubeconfig has been set: {}", kubeconfig);
            this.setupKubeConfig(kubeconfig);
            this.kubeAuthType = KubeAuthType.KUBECONFIG;

            LOGGER.debug("auth type has been set to: {}", this.kubeAuthType);
        }
    }

    public void destroy() {
        LOGGER.debug("Destroy Kube Auth Type");
        if (this.kubeAuthType == KubeAuthType.KUBECONFIG) {
            Path kubeconfigPath = null;
            try {
                kubeconfigPath = Files.createTempFile(KUBE_CONFIG_FILENAME, KUBE_CONFIG_EXTENSION);

                Files.deleteIfExists(kubeconfigPath);
                LOGGER.debug("Kubeconfig has been destroyed: {}", kubeconfigPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
