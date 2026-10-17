#!/usr/bin/env python3
"""Prueba de carga sencilla (sin instalar nada): N peticiones concurrentes a un endpoint.

Uso:  python3 scripts/prueba-carga.py URL [peticiones] [hilos]
Ej.:  python3 scripts/prueba-carga.py http://localhost:8090/catalog-service/api/v1/servicios/activos 2000 20
Muestra rendimiento (peticiones/s) y latencias p50, p95 y p99 en milisegundos.
"""
import statistics
import sys
import time
import urllib.request
from concurrent.futures import ThreadPoolExecutor

url = sys.argv[1]
total = int(sys.argv[2]) if len(sys.argv) > 2 else 1000
hilos = int(sys.argv[3]) if len(sys.argv) > 3 else 20


def peticion(_):
    inicio = time.perf_counter()
    with urllib.request.urlopen(url, timeout=10) as r:
        r.read()
        ok = r.status == 200
    return (time.perf_counter() - inicio) * 1000, ok


# Calentamiento: la JVM compila el código caliente y se llenan cachés y pools
with ThreadPoolExecutor(hilos) as ex:
    list(ex.map(peticion, range(200)))

inicio = time.perf_counter()
with ThreadPoolExecutor(hilos) as ex:
    resultados = list(ex.map(peticion, range(total)))
duracion = time.perf_counter() - inicio

lat = sorted(r[0] for r in resultados)
errores = sum(1 for r in resultados if not r[1])
pct = lambda p: lat[min(len(lat) - 1, int(len(lat) * p))]
print(f"peticiones={total} hilos={hilos} errores={errores}")
print(f"rendimiento={total / duracion:.0f} req/s  p50={statistics.median(lat):.1f} ms  "
      f"p95={pct(0.95):.1f} ms  p99={pct(0.99):.1f} ms")
