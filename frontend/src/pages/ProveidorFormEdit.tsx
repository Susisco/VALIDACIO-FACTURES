import React, { useEffect } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import {
  Paper,
  TextInput,
  Button,
  Flex,
  Group,
  Title,
  Loader,
  Text,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX } from "@tabler/icons-react";
import { useProveidor, useUpdateProveidor } from "../api/proveidors";

type FormValues = {
  nomComercial: string;
  nom: string;
  nif: string;
  adreca: string;
};

export default function ProveidorFormEdit() {
  const { id } = useParams<{ id: string }>();
  const proveidorId = Number(id);
  const navigate = useNavigate();

  const { data: item, isLoading, error } = useProveidor(proveidorId);
  const updateMutation = useUpdateProveidor();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>();

  useEffect(() => {
    if (item) {
      reset({
        nomComercial: item.nomComercial,
        nom: item.nom,
        nif: item.nif,
        adreca: item.adreca,
      });
    }
  }, [item, reset]);

  const onSubmit: SubmitHandler<FormValues> = (data) => {
    updateMutation.mutate(
      { id: proveidorId, data },
      {
        onSuccess: () => {
          showNotification({
            title: "Correcte",
            message: "Proveïdor actualitzat correctament",
            icon: <IconCheck size={18} />,
            color: "teal",
          });
          navigate("/proveidors");
        },
        onError: (err: unknown) => {
          const msg = err instanceof Error ? err.message : String(err);
          showNotification({
            title: "Error",
            message: msg,
            icon: <IconX size={18} />,
            color: "red",
          });
        },
      }
    );
  };

  if (isLoading || !item) return <Loader />;
  if (error) return <Text color="red">Error: {error.message}</Text>;

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{ maxWidth: 600, margin: "auto", fontFamily: "Poppins, sans-serif" }}
    >
      <Title order={3} mb="md">
        Editar Proveïdor #{proveidorId}
      </Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Flex direction="column" gap="md">
          <TextInput
            size="sm"
            label="Nom Comercial"
            placeholder="Nom comercial del proveïdor"
            {...register("nomComercial", {
              required: "El nom comercial és obligatori",
            })}
            error={errors.nomComercial?.message}
          />
          <TextInput
            size="sm"
            label="Nom"
            placeholder="Nom del proveïdor"
            {...register("nom", {
              required: "El nom és obligatori",
            })}
            error={errors.nom?.message}
          />
          <TextInput
            size="sm"
            label="NIF"
            placeholder="NIF del proveïdor"
            {...register("nif", {
              required: "El NIF és obligatori",
              pattern: {
                value: /^[A-Z0-9]{8,20}$/,
                message: "El NIF no és vàlid",
              },
            })}
            error={errors.nif?.message}
          />
          <TextInput
            size="sm"
            label="Adreça"
            placeholder="Adreça del proveïdor"
            {...register("adreca", {
              required: "L'adreça és obligatòria",
            })}
            error={errors.adreca?.message}
          />

<div style={{ marginTop: '24px' }}>
  <Group justify="apart">
    <Button
      size="sm"
      type="submit"
      loading={updateMutation.status === "pending"}
    >
      Desa
    </Button>
    <Button
      size="sm"
      variant="outline"
      onClick={() => navigate("/albarans")}
    >
      Cancel·la
    </Button>
  </Group>
</div>

        </Flex>
      </form>
    </Paper>
  );
}