# Kubernetes Action

Deploy to a Kubernetes Cluster using GitHub action

Usage example

```githubexpressionlanguage

      - name: Deploy to cluster
        uses: shailahir/kubernetes-action@v0.1.5
        with:
          kubeConfig: ${{ secrets.KUBE_CONFIG }}

```

## Currently supports:

Only prints the pods in the console

## Future plan:

- Kubectl full support (Opinionated)
