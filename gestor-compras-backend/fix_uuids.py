import re
import uuid
import os

def fix_uuids_in_file(filepath):
    """Fix all invalid UUIDs in a JasperReport XML file"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find all uuid="something" patterns
    def replace_uuid(match):
        old_uuid = match.group(1)
        # Check if it's already a valid UUID format
        try:
            uuid.UUID(old_uuid)
            return match.group(0)  # Already valid, keep it
        except ValueError:
            # Generate a new valid UUID
            new_uuid = str(uuid.uuid4())
            return f'uuid="{new_uuid}"'

    # Replace all UUIDs
    fixed_content = re.sub(r'uuid="([^"]+)"', replace_uuid, content)

    # Write back
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(fixed_content)

    print(f"Fixed UUIDs in {filepath}")

# Fix all report files
reports_dir = "src/main/resources/relatorios"
report_files = [
    "dashboard_executivo.jrxml",
    "itens_mais_solicitados.jrxml",
    "comparativo_cotacao.jrxml",
    "solicitacoes_abertas.jrxml",
    "pedidos_fechados.jrxml"
]

for report_file in report_files:
    filepath = os.path.join(reports_dir, report_file)
    if os.path.exists(filepath):
        fix_uuids_in_file(filepath)
    else:
        print(f"File not found: {filepath}")

print("Done!")
