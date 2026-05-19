package com.shailahir.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class KubeAuth {

    private static final String KUBE_CONFIG_FILENAME = "kubeconfig";
    private static final String KUBE_CONFIG_EXTENSION = ".yml";

    public static final String KUBECONFIG_FILE = KUBE_CONFIG_FILENAME + KUBE_CONFIG_EXTENSION;

    private KubeAuthType kubeAuthType;

    private void setupKubeConfig(String kubeConfigFile) {

        try {
            Path kubeconfigPath = Files.createTempFile(KUBE_CONFIG_FILENAME, KUBE_CONFIG_EXTENSION);

            Files.writeString(kubeconfigPath, kubeConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public KubeAuthType getKubeAuthType() {
        return this.kubeAuthType;
    }

    public void setup() {
        String kubeconfig = System.getenv("INPUT_KUBECONFIG");

        if (kubeconfig != null && !kubeconfig.trim().isEmpty()) {
            this.setupKubeConfig(kubeconfig);
            this.kubeAuthType = KubeAuthType.KUBECONFIG;
        }
    }

    public void destroy() {
        if (this.kubeAuthType == KubeAuthType.KUBECONFIG) {
            Path kubeconfigPath = null;
            try {
                kubeconfigPath = Files.createTempFile(KUBE_CONFIG_FILENAME, KUBE_CONFIG_EXTENSION);

                Files.deleteIfExists(kubeconfigPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
