# Kubernetes Action

Production-grade Kubernetes deployment GitHub Action.

This action allows you to:

* Apply Kubernetes manifests
* Deploy multiple manifest files
* Trigger rollout restarts
* Wait for deployments to become healthy
* Fail CI/CD pipelines on unhealthy rollouts
* Display pod status after deployment
* Support namespace-aware deployments

---

# Features

* Kubernetes manifest deployment
* Multiple deployment file support
* Rollout restart support
* Rollout status monitoring
* Deployment health verification
* Namespace support
* Dry-run support
* Pod status printing
* GitHub Actions friendly logging

---

# Usage

## Basic Deployment

```yaml
- name: Deploy to cluster
  uses: shailahir/kubernetes-action@v0.1.17

  with:
    kubeConfig: ${{ secrets.KUBE_CONFIG }}

    deployments: |
      ./kubernetes/deployment.yml
```

---

# Deploy Multiple Files

```yaml
- name: Deploy to cluster
  uses: shailahir/kubernetes-action@v0.1.17

  with:
    kubeConfig: ${{ secrets.KUBE_CONFIG }}

    deployments: |
      ./kubernetes/namespace.yml;
      ./kubernetes/configmap.yml;
      ./kubernetes/deployment.yml;
      ./kubernetes/service.yml

    deploymentsDelimiter: ';'
```

---

# Production Example

```yaml
- name: Deploy Application
  uses: shailahir/kubernetes-action@v0.1.17

  with:
    kubeConfig: ${{ secrets.KUBE_CONFIG }}

    deployments: |
      ./kubernetes/deployment.yml,
      ./kubernetes/service.yml,
      ./kubernetes/ingress.yml

    namespace: production

    rolloutDeployments: backend-api

    rolloutTimeout: 300s

    showPods: true
```

---

# Inputs

| Input                | Required | Default   | Description                          |
| -------------------- | -------- | --------- | ------------------------------------ |
| kubeConfig           | Yes      | -         | Kubernetes kubeconfig content        |
| deployments          | No       | -         | Deployment manifest files            |
| deploymentsDelimiter | No       | `,`       | Delimiter for deployment files       |
| namespace            | No       | `default` | Kubernetes namespace                 |
| rolloutDeployments   | No       | -         | Deployment names for rollout restart |
| rolloutTimeout       | No       | `300s`    | Rollout wait timeout                 |
| dryRun               | No       | `false`   | Run kubectl apply with dry-run       |
| showPods             | No       | `true`    | Print pod status after deployment    |

---

# Example GitHub Secret

Store your kubeconfig in:

```text
KUBE_CONFIG
```

Example:

```yaml
kubeConfig: ${{ secrets.KUBE_CONFIG }}
```

---

# Rollout Health Verification

The action waits for deployments to become healthy before succeeding.

The workflow fails automatically if:

* rollout fails
* pods crash
* readiness probes fail
* deployment timeout occurs

This makes the action safe for production CI/CD pipelines.

---

# Example CI/CD Flow

```text
Build Application
      ↓
Build Docker Image
      ↓
Push Docker Image
      ↓
Deploy To Kubernetes
      ↓
Wait For Healthy Rollout
      ↓
Success / Failure
```

---

# Roadmap

Planned future improvements:

* Helm support
* Kustomize support
* StatefulSet support
* ConfigMap patching
* Secret management
* Ingress validation
* Rollback support
* Canary deployments
* Blue/Green deployments
* Full kubectl command support
* GitOps integration

---

# License

MIT
