# Kubernetes Action

Deploy to a Kubernetes Cluster using Github action

Usage example

```githubexpressionlanguage

      - name: Deploy to cluster
        uses: shailahir/kubernetes-action@v0.1.4
        with:
          kubeConfig: ${{ secrets.KUBE_CONFIG }}

```

## Currently supports:

Only prints the pods in the console

## Future plan:

- Kubectl full support (Opinionated)
- 