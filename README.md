# Kubernetes Project 2: DevSecOps Hardening Engine

## Defensive Microservice Architecture with Trivy Scanning, Least-Privilege RBAC, Zero-Trust NetworkPolicies, and Hardened Kubernetes Runtime Security

---

## Project Overview

This repository contains the infrastructure blueprints and security pipeline automation for a production-grade **DevSecOps Hardening Engine**. In modern cloud-native environments, deploying application workloads without defensive guardrails exposes underlying infrastructure to container breakout attacks, unauthorized lateral network movement, privilege escalation, and supply chain vulnerabilities.

This project implements a **defense-in-depth framework** across the entire software delivery lifecycle. By embedding automated security scanners into the delivery pipeline and enforcing strict OS-level kernel isolation profiles, Least-Privilege Role-Based Access Control (RBAC), and Zero-Trust NetworkPolicies, the platform guarantees that workloads execute with minimal privilege boundaries, immutable filesystems, and isolated network perimeters.

---

## Repository Name

```text
devsecops-hardening-engine
```

---

## Project Summary

```text
Defensive microservice architecture, automated static vulnerability scanning gates (Trivy), Least-Privilege RBAC, Zero-Trust network micro-segmentation, and kernel-level container security context enforcement on Kubernetes.
```

---

## System Architecture

The architecture enforces security guardrails at every layer of the deployment stack, combining shift-left static analysis with runtime kernel isolation.

* **CI/CD Security Gate:** Static vulnerability scanners such as Trivy analyze container images and Kubernetes manifests before cluster deployment, intercepting misconfigurations and high-severity CVEs.

* **Least-Privilege Identity Tier:** Dedicated ServiceAccounts replace default service credentials, bound strictly to fine-grained RBAC Roles limiting API server access.

* **Zero-Trust Network Firewall:** Declarative NetworkPolicies implement a Default-Deny-All ingress rule, explicitly whitelisting authorized communication channels between microservices.

* **Hardened Runtime Container:** Application containers run under an unprivileged non-root user ID (`UID 10001`), drop all Linux kernel capabilities, enforce `readOnlyRootFilesystem: true`, and utilize `RuntimeDefault` Seccomp profiles.

---

## Architecture Diagram

```text
+------------------------------------------------------------------------------------+
| SHIFT-LEFT SECURITY PIPELINE GATE                                                  |
| Source Code & Manifests ---> Trivy Static Analysis ---> Registry (sun003/secure)   |
+------------------------------------------------------------------------------------+
                                         |
                                         v (kubectl apply)
+------------------------------------------------------------------------------------+
| KUBERNETES HARDENED CLUSTER DOMAIN                                                 |
|                                                                                    |
|  [ Least-Privilege RBAC Tier ]                                                     |
|   ServiceAccount (secure-app-sa) <---> RoleBinding <---> Role (Pod Reader)         |
|                                                                                    |
|  [ Zero-Trust Network Micro-Segmentation ]                                         |
|   Default-Deny Ingress Policy  <--- [ Blocked: Unauthorized Attacker Pod ]         |
|   Allow-Secure Ingress Policy  <--- [ Allowed: Authorized Inbound Port 8080 ]      |
|                                                                                    |
|  [ Hardened Pod Runtime Container ]                                                |
|   +----------------------------------------------------------------------------+   |
|   | Container Name: secure-processor                                           |   |
|   | User Context: runAsUser 10001 (Non-Root Execution)                         |   |
|   | Storage Context: readOnlyRootFilesystem = true                             |   |
|   | Kernel Context: Capabilities = DROP ALL, Seccomp = RuntimeDefault          |   |
|   +----------------------------------------------------------------------------+   |
+------------------------------------------------------------------------------------+
```

---

## Repository Structure and Component Breakdown

```text
devsecops-hardening-engine/
├── app/
│   ├── server.js
│   ├── package.json
│   ├── package-lock.json
│   └── Dockerfile
└── k8s/
    ├── pipeline.groovy
    ├── rbac.yaml
    ├── network-policy.yaml
    └── deployment.yaml
```

---

## Application Compute Tier (`app/`)

### `server.js`

Houses the hardened Express.js processing microservice exposing the `/secure-data` route alongside dedicated `/healthz` health diagnostic probes. It runs stateless without requiring local disk write access.

### `package.json`

Defines minimal production backend dependencies with strict semantic versioning to eliminate unneeded package bloat and reduce supply chain attack vectors.

### `package-lock.json`

Deterministic lockfile ensuring exact dependency tree installations across automated container builds.

### `Dockerfile`

Multi-stage build configuration engineered for container hardening. Stage 1 compiles application modules, while Stage 2 copies production assets into a clean, unprivileged runtime base, explicitly setting execution permissions to non-root user `node`.

---

## Declarative Orchestration & Security Tier (`k8s/`)

### `pipeline.groovy`

Jenkins/CI automation pipeline script integrating Trivy vulnerability scanning stages to evaluate Kubernetes manifests and container images prior to deployment execution.

### `rbac.yaml`

Implements Role-Based Access Control enforcing the Principle of Least Privilege. Configures a dedicated ServiceAccount (`secure-app-sa`), a scoped Role restricting API access to read-only pod operations, and a RoleBinding attaching the identity.

### `network-policy.yaml`

Zero-Trust micro-segmentation blueprint enforcing a default-deny ingress firewall (`default-deny-all`) alongside a target whitelisting policy (`allow-secure-ingress`) that explicitly permits inbound TCP traffic on port `8080` only from authorized application selectors.

### `deployment.yaml`

Production deployment manifest incorporating complete container security contexts:

* `runAsNonRoot: true`
* `runAsUser: 10001`
* `runAsGroup: 10001`
* `readOnlyRootFilesystem: true`
* `capabilities.drop: ALL`
* `seccompProfile: RuntimeDefault`
* Explicit CPU and memory resource boundaries
* LoadBalancer service exposure through `secure-compute-service`

---

## Core Engineering Focus Areas

### 1. CI/CD Static Analysis & Infrastructure Policy Enforcement

Deploying unvetted manifests or vulnerable container base images introduces critical security flaws into production clusters. This platform embeds shift-left security analysis directly into the software delivery pipeline using Trivy.

Before any deployment manifest is accepted by the Kubernetes control plane, static analysis scanners evaluate configuration parameters against Pod Security Standards (PSS) and CIS Kubernetes Benchmarks. The scanner inspects image layers for known CVEs and flags dangerous manifest definitions, such as missing resource limits, root user execution, privileged execution flags, or untrusted image registry URIs. Policy violations above designated risk thresholds automatically fail build jobs, intercepting insecure code before execution.

---

### 2. Least-Privilege Access Control & RBAC Boundaries

Relying on default Kubernetes service accounts grants workloads unnecessary access to the cluster API server, enabling potential lateral movement if a pod is compromised.

This architecture disables default token mounting and creates a dedicated ServiceAccount (`secure-app-sa`). A custom RBAC Role isolates API permissions strictly to read-only operations (`get`, `list`, `watch`) on pod resources within the local namespace. The RoleBinding links this identity explicitly to the application deployment, ensuring that compromised container runtimes cannot escalate privileges or manipulate cluster-wide resources.

---

### 3. Zero-Trust Network Micro-Segmentation & Traffic Whitelisting

By default, Kubernetes enforces a flat, open network model where any pod can communicate with any other pod across namespaces. To prevent unauthorized lateral movement, this infrastructure enforces Zero-Trust micro-segmentation.

The network layer deploys a baseline `default-deny-all` NetworkPolicy that drops all unwhitelisted inbound traffic across the namespace. A secondary NetworkPolicy (`allow-secure-ingress`) explicitly opens port `8080` only for ingress traffic matching specific application label selectors (`app: secure-compute-layer`). Unauthorized port scans or connection attempts from external or compromise-suspected pods are dropped at the CNI network boundary.

---

### 4. Container OS Hardening & Runtime Security Context Enforcement

If an attacker achieves remote code execution inside a container, OS-level hardening controls limit the blast radius by preventing filesystem modifications and privilege escalation.

The deployment manifest enforces strict security contexts at both pod and container runtime levels:

* **Non-Root Runtime Isolation:** `runAsNonRoot: true` and `runAsUser: 10001` force the container process to run as an unprivileged UID, preventing host root compromise.

* **Read-Only Root Filesystem:** `readOnlyRootFilesystem: true` mounts the entire container filesystem as immutable, blocking attackers from dropping web shells, modifying system binaries, or writing malicious scripts.

* **Capability Stripping:** `capabilities.drop: ALL` strips all Linux kernel capabilities such as `CAP_SYS_ADMIN` and `CAP_NET_RAW`, neutralizing low-level kernel exploit vectors.

* **Seccomp Profiling:** `seccompProfile.type: RuntimeDefault` restricts system call execution to standard, safe syscall subsets.

---

## Technical Difficulties Faced and Resolutions

### 1. Non-Elevated Shell Privilege Failures During Scanner Installation

**Issue:** Executing native package manager commands such as `choco install trivy` inside a non-elevated PowerShell terminal resulted in `System.UnauthorizedAccessException` errors due to restricted access to `C:\ProgramData\chocolatey\lib-bad`.

**Resolution:** Bypassed local host OS installation dependencies entirely by executing Trivy inside a containerized ephemeral Docker runtime:

```bash
docker run --rm -v "${PWD}:/k8s" aquasec/trivy config /k8s/deployment.yaml
```

This allowed seamless, cross-platform security scanning without requiring administrative host rights.

---

### 2. Local Kubernetes API Server Refusal During Resource Application

**Issue:** Applying RBAC manifests via `kubectl apply -f rbac.yaml` failed with:

```text
dial tcp 127.0.0.1:1958: connectex: No connection could be made because the target machine actively refused it
```

This indicated that the Kubernetes API server was unreachable.

**Resolution:** Diagnostic checks confirmed the Minikube cluster instance was inactive. Executed the following command to initialize the control plane components:

```bash
minikube start
```

Then verified connection health:

```bash
kubectl cluster-info
```

After the API server became reachable, the security manifests were applied successfully.

---

### 3. Manifest Security Policy Violations Across Unhardened Pod Definitions

**Issue:** Initial Trivy scans on generic deployment manifests flagged 11 misconfigurations, including:

* `KSV-0118` HIGH for default privileged security contexts
* `KSV-0030` / `KSV-0104` MEDIUM for missing Seccomp profiles
* `KSV-0125` for untrusted registry domains

**Resolution:** Completely updated `k8s/deployment.yaml` by declaring explicit pod-level and container-level securityContext parameters:

```yaml
runAsUser: 10001
seccompProfile:
  type: RuntimeDefault
readOnlyRootFilesystem: true
capabilities:
  drop:
    - ALL
```

Defined CPU/memory resource limits and requests, and prefixed image URIs with `docker.io/`. Re-scanning verified the resolution of all High and Critical findings.

---

### 4. Network Timeout Validation During Network Policy Testing

**Issue:** Verifying that `default-deny-all` NetworkPolicies actively blocked unauthorized traffic required distinguishing between legitimate application responses and dropped packets without hanging terminal sessions indefinitely.

**Resolution:** Executed an unapproved test pod with an explicit timeout parameter:

```bash
kubectl run unauthorized-attacker --image=busybox --restart=Never -- wget -qO- --timeout=3 http://secure-compute-service
```

The active NetworkPolicy dropped the ingress packets, causing `wget` to time out and exit with `Status: Error`, confirming successful policy enforcement.

---

## Deployment and Verification Plan

### 1. Shift-Left Security Gate Execution

Run static vulnerability analysis against local manifests:

```bash
docker run --rm -v "${PWD}:/k8s" aquasec/trivy config /k8s/deployment.yaml
```

Verify zero High or Critical vulnerabilities before applying code to the cluster.

---

### 2. Access Control & Identity Deployment

Apply Least-Privilege identity definitions:

```bash
kubectl apply -f rbac.yaml
```

This establishes the `secure-app-sa` ServiceAccount and scoped API Role boundaries.

---

### 3. Zero-Trust Network Firewall Initialization

Deploy micro-segmentation policies:

```bash
kubectl apply -f network-policy.yaml
```

This establishes the default-deny baseline across the cluster namespace.

---

### 4. Hardened Workload Execution

Apply the hardened deployment manifest:

```bash
kubectl apply -f deployment.yaml
```

The control plane provisions non-root pods with immutable filesystems, dropped capabilities, and active liveness probes.

---

## Production Verification Proofs

### 1. Automated Pipeline & Clean Scan Output

Static configuration scans using Trivy demonstrate that the hardened `deployment.yaml` manifest successfully resolves all critical OS kernel, privilege escalation, and resource boundary security risks, passing the security gate with **0 High** and **0 Critical** vulnerabilities.

<img width="1231" height="677" alt="Screenshot 1" src="https://github.com/user-attachments/assets/7403fee8-5a71-475a-8dc1-e94904863cbe" />
<img width="1045" height="337" alt="Screenshot 2" src="https://github.com/user-attachments/assets/379ae9bf-ac05-4a3e-b84c-e07fa7bfa68b" />



---

### 2. Vulnerability Gate Intercept & Policy Violation

To verify policy enforcement, Trivy was executed against an intentionally insecure manifest (`test-insecure.yaml`). The scanner actively intercepted and flagged **19 misconfigurations**, including **3 HIGH-severity violations**:

* `KSV-0017` Privileged Container
* `KSV-0118` Default Security Context
* `KSV-0014` Missing Read-Only Filesystem

This proves the pipeline halts unsafe code deployment.

<img width="1237" height="913" alt="Screenshot 3" src="https://github.com/user-attachments/assets/46c28018-9d2f-4a20-99f2-6ae1613fdef4" />
<img width="1047" height="804" alt="Screenshot 4" src="https://github.com/user-attachments/assets/466f1372-bf65-4547-8277-a93bd7135e5d" />
<img width="1495" height="795" alt="Screenshot 5" src="https://github.com/user-attachments/assets/7e33e033-0a6b-49e7-af43-90498f0ab0b0" />
<img width="1249" height="792" alt="Screenshot 6" src="https://github.com/user-attachments/assets/2dd4d10f-4080-4e7e-98e0-b87bffb655c8" />
<img width="975" height="792" alt="Screenshot 7" src="https://github.com/user-attachments/assets/5f094f35-3a57-4a40-87b6-ccb933000f00" />
<img width="962" height="763" alt="Screenshot 8" src="https://github.com/user-attachments/assets/5cc3633b-bd7d-4e2f-94cc-47ee2e27538f" />
<img width="1609" height="542" alt="Screenshot 9" src="https://github.com/user-attachments/assets/9fcfba06-f8fd-454a-9276-31084c2b9a61" />


---

### 3. Network Micro-Segmentation & Traffic Block

Executing an unapproved test pod (`unauthorized-attacker`) attempting to reach `secure-compute-service` triggers an active connection drop due to the `default-deny-all` NetworkPolicy. The command times out and terminates with `Status: Error`, confirming that unwhitelisted cross-namespace ingress traffic is blocked at the network layer.

<img width="1536" height="340" alt="Screenshot 10" src="https://github.com/user-attachments/assets/6608bb53-08f5-4bbf-b50a-59ec1fd9fc21" />


---

### 4. Container OS Hardening & Security Context Enforcement

Executing interactive inspection commands inside the running pod verifies kernel isolation controls.

#### User Isolation

```bash
kubectl exec -it deployment/secure-compute-deployment -- whoami
```

Expected output:

```text
whoami: unknown uid 10001
```

This confirms execution under an unprivileged non-root UID.

#### Filesystem Immutability

```bash
kubectl exec -it deployment/secure-compute-deployment -- touch /test.txt
```

Expected output:

```text
touch: /test.txt: Read-only file system
```

This proves write operations are blocked across the root filesystem.

<img width="1207" height="177" alt="Screenshot 11" src="https://github.com/user-attachments/assets/9ab6a0ab-5576-4055-9e31-bd573d72a15b" />


---

## Future Improvements

Expand the DevSecOps pipeline into an enterprise-grade runtime security environment by introducing automated admission controllers such as **Kyverno** or **Open Policy Agent Gatekeeper** to enforce policy-as-code at the cluster API boundary.

Additional improvements include integrating **Falco** for real-time kernel syscall threat detection, establishing automated secret management through **HashiCorp Vault**, and integrating **GitHub Actions** for continuous container signing using **Cosign/Sigstore**.
